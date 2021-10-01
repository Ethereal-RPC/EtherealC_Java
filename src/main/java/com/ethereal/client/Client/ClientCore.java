package com.ethereal.client.Client;

import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.RequestCore;

public class ClientCore {

    public static Client get(String netName, String serviceName)  {
        Net net = NetCore.get(netName);//获取对应的网络节点
        if(net != null){
            return get(net,serviceName);
        }
        else return null;
    }

    public static Client get(Net net, String serviceName)  {
        Request request = RequestCore.get(net,serviceName);
        if(request != null){
            return  request.getClient();
        }
        else return null;
    }
    public static Client register(Net net, String serviceName, Client client) throws TrackException {
        Request request = RequestCore.get(net,serviceName);
        if(request != null){
            return register(request,client);
        }
        else throw new TrackException(TrackException.ErrorCode.Core, String.format("%s-%s 未找到！", net.getName(),serviceName));
    }

    public static Client register(Request request, Client client) throws TrackException {
        if(request.getClient() == null){
            request.setClient(client);
            client.setNetName(request.getNetName());
            client.setServiceName(request.getName());
            client.getLogEvent().register(request::onLog);//日志系统
            client.getExceptionEvent().register(request::onException);//异常系统
            client.getConnectSuccessEvent().register(value -> {
                Request _request = RequestCore.get(value.getNetName(), value.getServiceName());
                if(_request!=null)
                {
                    _request.onConnectSuccess();
                }
            });
        }
        return client;
    }

    public static boolean unregister(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        return unregister(net,serviceName);
    }

    public static boolean unregister(Net net,String serviceName)  {
        if(net!=null){
            Request request = RequestCore.get(net,serviceName);
            return unregister(request);
        }
        else return true;
    }
    public static boolean unregister(Request request)  {
        if(request != null){
            Client echoClient= request.getClient();
            if(echoClient != null){
                echoClient.disConnect();
                request.setClient(null);
                return true;
            }
        }
        return true;
    }
}
