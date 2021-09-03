import Model.*;
import NativeClient.ClientConfig;
import NativeClient.ClientCore;
import NativeClient.Event.Delegate.OnConnectFailDelegate;
import NativeClient.Event.Delegate.OnConnectSuccessDelegate;
import NativeClient.SocketClient;
import RPCNet.Event.Delegate.OnExceptionDelegate;
import RPCNet.Event.Delegate.OnLogDelegate;
import RPCNet.Net;
import RPCNet.NetCore;
import RPCRequest.Request;
import RPCRequest.RequestCore;
import RPCService.Service;
import RPCService.ServiceCore;
import RequestDemo.ServerRequest;
import ServiceDemo.ClientService;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.javatuples.Triplet;
import org.javatuples.Tuple;

import java.io.Console;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

public class Demo {
    public static void main(String[] args) throws Exception {
        //单节点
        //single("127.0.0.1","28015","1");
        //分布式
        netNode("demo","127.0.0.1");
    }
    public static void single(String ip,String port,String netName) throws Exception {
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
        ((Request) Proxy.getInvocationHandler(serverRequest)).getConnectSuccessEvent().register(request -> {
            ServerRequest _request = (ServerRequest)RequestCore.get(net,"Server");
            Integer result = ((ServerRequest)_request).Add(3,4);
            System.out.println("结果值是:" + result);
        });

        SocketClient socketClient = ClientCore.register((Request) Proxy.getInvocationHandler(serverRequest),ip,port);
        socketClient.getConnectSuccessEvent().register(new OnConnectSuccessDelegate() {
            @Override
            public void OnConnectSuccess(SocketClient client) {
                System.out.println("Single启动成功");
            }
        });
        socketClient.getConnectFailEvent().register(new OnConnectFailDelegate() {
            @Override
            public void OnConnectFail(SocketClient client) throws Exception {
                System.out.println("Single启动失败");
            }
        });
        //关闭分布式
        net.getConfig().setNetNodeMode(false);
        //启动服务
        net.publish();
    }
    public static void netNode(String netName,String ip) throws Exception {
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
        ArrayList<Triplet<String ,String , ClientConfig>> ips = new ArrayList<>();
        ips.add(new Triplet<>(ip,"28015",new ClientConfig()));
        ips.add(new Triplet<>(ip,"28016",new ClientConfig()));
        ips.add(new Triplet<>(ip,"28017",new ClientConfig()));
        ips.add(new Triplet<>(ip,"28018",new ClientConfig()));
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
