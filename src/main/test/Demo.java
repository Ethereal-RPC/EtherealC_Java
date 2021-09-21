import Core.Enums.NetType;
import Core.Event.Delegate.LogEventDelegte;
import Core.Model.AbstractTypes;
import Core.Model.TrackLog;
import Core.Model.User;
import Client.Abstract.Client;
import Client.Abstract.ClientConfig;
import Client.ClientCore;
import Client.WebSocket.WebSocketClientConfig;
import Net.Abstract.Net;
import Net.NetCore;
import Request.Abstract.Request;
import Request.RequestCore;
import Service.Abstract.Service;
import Service.ServiceCore;
import RequestDemo.ServerRequest;
import ServiceDemo.ClientService;
import org.javatuples.Pair;

import java.lang.reflect.Proxy;
import java.util.ArrayList;

public class Demo {
    public static void main(String[] args) throws Exception {
        //单节点
        //single("127.0.0.1:28015/NetDemo/","1");
        //分布式
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("127.0.0.1:28015/NetDemo/");
        arrayList.add("127.0.0.1:28016/NetDemo/");
        arrayList.add("127.0.0.1:28017/NetDemo/");
        arrayList.add("127.0.0.1:28018/NetDemo/");
        netNode("demo",arrayList);
    }
    public static void single(String prefixes,String netName) throws Exception {
        AbstractTypes types = new AbstractTypes();
        types.add(Integer.class,"Int");
        types.add(Long.class,"Long");
        types.add(String.class,"String");
        types.add(Boolean.class,"Bool");
        types.add(User.class,"User");
        Net net = NetCore.register(netName, NetType.WebSocket);
        net.getExceptionEvent().register(exception -> System.out.println(exception.getMessage()));
        net.getLogEvent().register(log -> System.out.println(log.getMessage()));
        //向网关注册服务
        Service service = ServiceCore.register(ClientService.class,net,"Client",types);
        //向网关注册请求
        ServerRequest serverRequest = RequestCore.register(ServerRequest.class,net,"Server",types);
        ((Request) Proxy.getInvocationHandler(serverRequest)).getConnectSuccessEvent().register(request -> {
            ServerRequest _request = (ServerRequest)RequestCore.get(net,"Server");
            Integer result = ((ServerRequest)_request).Add(3,4);
            System.out.println("结果值是:" + result);
        });

        Client socketClient = ClientCore.register((Request) Proxy.getInvocationHandler(serverRequest),prefixes);
        socketClient.getConnectEvent().register(client -> System.out.println("Single启动成功"));
        socketClient.getDisConnectEvent().register(client -> System.out.println("Single启动失败"));
        //关闭分布式
        net.getConfig().setNetNodeMode(false);
        //启动服务
        net.publish();
    }
    public static void netNode(String netName,ArrayList<String> prefixes) throws Exception {
        AbstractTypes types = new AbstractTypes();
        types.add(Integer.class,"Int");
        types.add(User.class,"User");
        types.add(Long.class,"Long");
        types.add(String.class,"String");
        types.add(Boolean.class,"Bool");
        Net net = NetCore.register(netName,NetType.WebSocket);
        net.getExceptionEvent().register((exception -> System.out.println(exception.getException().getMessage())));
        net.getLogEvent().register(log -> System.out.println(log.getMessage()));
        //向网关注册服务
        Service service = ServiceCore.register(ClientService.class,net,"Client",types);
        //向网关注册请求
        ServerRequest serverRequest = RequestCore.register(ServerRequest.class,net,"Server",types);
        ((Request)Proxy.getInvocationHandler(serverRequest)).onConnectSuccess();
        //开启分布式
        net.getConfig().setNetNodeMode(true);
        ArrayList<Pair<String, ClientConfig>> ips = new ArrayList<>();
        for (String item : prefixes){
            ips.add(new Pair<>(item,new WebSocketClientConfig()));
        }
        net.getConfig().setNetNodeIps(ips);
        //启动服务
        net.publish();
        ((Request) Proxy.getInvocationHandler(serverRequest)).getConnectSuccessEvent().register(request -> {
            ServerRequest _request = (ServerRequest)RequestCore.get(net,"Server");
            Integer result = ((ServerRequest)_request).Add(3,4);
            System.out.println("结果值是:" + result);
        });
    }
}
