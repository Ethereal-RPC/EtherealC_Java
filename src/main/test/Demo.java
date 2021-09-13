import Model.*;
import NativeClient.ClientConfig;
import NativeClient.ClientCore;
import NativeClient.Event.Delegate.OnDisConnectDelegate;
import NativeClient.Event.Delegate.OnConnectDelegate;
import NativeClient.Client;
import RPCNet.Net;
import RPCNet.NetCore;
import RPCRequest.Request;
import RPCRequest.RequestCore;
import RPCService.Service;
import RPCService.ServiceCore;
import RequestDemo.ServerRequest;
import ServiceDemo.ClientService;
import org.javatuples.Pair;
import org.javatuples.Triplet;

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
        RPCTypeConfig types = new RPCTypeConfig();
        types.add(Integer.class,"Int");
        types.add(Long.class,"Long");
        types.add(String.class,"String");
        types.add(Boolean.class,"Bool");
        types.add(User.class,"User");
        Net net = NetCore.register(netName);
        net.getExceptionEvent().register((exception, net1) -> System.out.println(exception.getMessage()));
        net.getLogEvent().register((log, net12) -> System.out.println(log.getMessage()));
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
        socketClient.getConnectEvent().register(new OnConnectDelegate() {
            @Override
            public void OnConnectSuccess(Client client) {
                System.out.println("Single启动成功");
            }
        });
        socketClient.getDisConnectEvent().register(new OnDisConnectDelegate() {
            @Override
            public void OnDisConnect(Client client) {
                System.out.println("Single启动失败");
            }
        });
        //关闭分布式
        net.getConfig().setNetNodeMode(false);
        //启动服务
        net.publish();
    }
    public static void netNode(String netName,ArrayList<String> prefixes) throws Exception {
        RPCTypeConfig types = new RPCTypeConfig();
        types.add(Integer.class,"Int");
        types.add(User.class,"User");
        types.add(Long.class,"Long");
        types.add(String.class,"String");
        types.add(Boolean.class,"Bool");
        Net net = NetCore.register(netName);
        net.getExceptionEvent().register((exception, net1) -> System.out.println(exception.getMessage()));
        net.getLogEvent().register((log, net12) -> System.out.println(log.getMessage()));
        //向网关注册服务
        Service service = ServiceCore.register(ClientService.class,net,"Client",types);

        //向网关注册请求
        ServerRequest serverRequest = RequestCore.register(ServerRequest.class,net,"Server",types);
        ((Request)Proxy.getInvocationHandler(serverRequest)).onConnectSuccess();
        //开启分布式
        net.getConfig().setNetNodeMode(true);
        ArrayList<Pair<String, ClientConfig>> ips = new ArrayList<>();
        for (String item : prefixes){
            ips.add(new Pair<>(item,new ClientConfig()));
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
