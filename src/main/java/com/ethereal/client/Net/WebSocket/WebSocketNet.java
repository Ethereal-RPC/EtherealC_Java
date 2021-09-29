package com.ethereal.client.Net.WebSocket;

import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Client.Abstract.ClientConfig;
import com.ethereal.client.Client.ClientCore;
import com.ethereal.client.Client.Event.Delegate.OnDisConnectDelegate;
import com.ethereal.client.Client.WebSocket.WebSocketClientConfig;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Net.NetNode.Model.NetNode;
import com.ethereal.client.Net.NetNode.Request.ServerNetNodeRequest;
import com.ethereal.client.Net.NetNode.Service.ClientNetNodeService;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.RequestCore;
import com.ethereal.client.Service.Abstract.Service;
import com.ethereal.client.Service.ServiceCore;
import org.javatuples.Pair;

import java.lang.reflect.Proxy;
import java.util.concurrent.Semaphore;

public class WebSocketNet extends Net {
    private Semaphore connectSign = new Semaphore(0);

    public WebSocketNetConfig getConfig() {
        return (WebSocketNetConfig)config;
    }


    public WebSocketNet(){
        netType = NetType.WebSocket;
    }
    @Override
    public boolean publish() throws java.lang.Exception {
        //分布式模式
        if(config.getNetNodeMode()){
            //注册数据类型
            AbstractTypes types = new AbstractTypes();
            types.add(Integer.class,"Int");
            types.add(Long.class,"Long");
            types.add(String.class,"String");
            types.add(Boolean.class,"Bool");
            types.add(NetNode.class,"NetNode");
            //注册网关
            Net net = NetCore.register(String.format("NetNode-%s", name),NetType.WebSocket);
            net.getLogEvent().register(this::onLog);
            net.getExceptionEvent().register(this::onException);
            //注册服务
            Service netNodeService = ServiceCore.register(ClientNetNodeService.class,net,"ClientNetNodeService",types);
            //注册请求
            ServerNetNodeRequest netNodeRequest = RequestCore.register(ServerNetNodeRequest.class,net,"ServerNetNodeService",types);
            new Thread(() -> {
                while (true){
                    try {
                        NetNodeSearch();
                    }
                    catch (java.lang.Exception e) {
                        onException(new TrackException(e));
                    }
                    finally {
                        try {
                            Thread.sleep(config.getNetNodeHeartInterval());
                        } catch (InterruptedException e) {
                            onException(new TrackException(e));
                        }
                    }
                }
            }).start();
        }
        else {
            for(Request request : requests.values()){
                request.getClient().connect();
            }
        }
        return true;
    }

    private void NetNodeSearch() throws TrackException, InterruptedException {
        synchronized (connectSign){
            boolean flag = false;
            for(Request request : requests.values()){
                if(request.getClient() == null){
                    flag = true;
                    break;
                }
            }
            if(flag){
                Client client = null;
                Net net = NetCore.get(String.format("NetNode-%s", name));
                if(net==null)throw new TrackException(TrackException.ErrorCode.Runtime, String.format("NetNode-%s 未找到", name));
                for (Pair<String, ClientConfig> item: config.getNetNodeIps()) {
                    String prefixes = item.getValue0();
                    ClientConfig config = item.getValue1();
                    client = ClientCore.register(net,"ServerNetNodeService",prefixes,config);
                    if(client == null)throw new TrackException(TrackException.ErrorCode.Runtime, String.format("%s-%s 服务未找到", net.getName(),"ServerNetNodeService"));
                    net.getConfig().setNetNodeMode(false);
                    ((WebSocketClientConfig)(client.getConfig())).setSyncConnect(false);
                    //启动连接
                    client.connect();
                    //连接成功
                    if(client.isConnect()){
                        break;
                    }
                    else {
                        ClientCore.unregister(net,"ServerNetNodeService");
                    }
                }
                if(client.isConnect()){
                    Request netNodeRequest = RequestCore.get(net,"ServerNetNodeService");
                    if(netNodeRequest == null)throw new TrackException(TrackException.ErrorCode.Runtime,String.format("%s-%s 查找不到该请求", name,"ServerNetNodeService"));
                    for (Request request : requests.values()) {
                        if(request.getClient() == null){
                            ServerNetNodeRequest serverNetNodeRequest = (RequestCore.get(net,"ServerNetNodeService"));
                            //获取服务节点
                            NetNode node = serverNetNodeRequest.GetNetNode("ServerNetNodeService");

                            if(node != null){
                                //注册连接并启动连接
                                Client requestClient = ClientCore.register(request,node.getPrefixes()[0]);
                                requestClient.getDisConnectEvent().register(new OnDisConnectDelegate() {
                                    @Override
                                    public void OnDisConnect(Client client){
                                        try{
                                            client.getDisConnectEvent().unRegister(this);
                                            ClientCore.unregister(client.getNetName(),client.getServiceName());
                                        }
                                        catch (java.lang.Exception e){
                                            client.onException(new TrackException(e));
                                        }
                                    }
                                });
                                requestClient.connect();
                            }
                            else {
                                throw new TrackException(TrackException.ErrorCode.Runtime, String.format("%s-%s 在NetNode分布式节点中未找到节点", name,request.getName()));
                            }
                        }
                    }
                }
                ClientCore.unregister(net,"ServerNetNodeService");
            }
        }
    }
}
