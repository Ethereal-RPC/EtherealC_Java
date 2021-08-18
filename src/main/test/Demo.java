import Model.RPCException;
import Model.RPCTypeConfig;
import Model.User;
import NativeClient.ClientCore;
import NativeClient.SocketClient;
import RPCNet.Net;
import RPCNet.NetCore;
import RPCRequest.RequestCore;
import RPCService.Service;
import RPCService.ServiceCore;
import RequestDemo.ServerRequest;
import ServiceDemo.ClientService;

import java.lang.reflect.InvocationTargetException;

public class Demo {
    public static void main(String[] args) throws RPCException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        //注册数据类型 wwr
        RPCTypeConfig types = new RPCTypeConfig();
        types.add(Integer.class,"int");
        types.add(User.class,"user");
        types.add(Long.class,"long");
        types.add(String.class,"string");
        types.add(Boolean.class,"bool");
        //建立网关
        Net net = NetCore.register("demo");
        //向网关注册服务
        Service service = ServiceCore.register(ClientService.class,net, "Client", types);
        //向网关注册请求
        ServerRequest request = RequestCore.register(ServerRequest.class,net, "Server", types);
        //向网关注册连接
        SocketClient client = ClientCore.register(net, "127.0.0.1", "28015");
        //启动连接
        client.start();
        //向目标网络执行注册请求
        request.Register("JavaClient",Long.parseLong("1"));
        //经目标网络发送至目标客户端消息
        if(request.SendSay(Long.parseLong("0"),"Hello C#")){
            System.out.println("消息发送成功");
        }
        else {
            System.out.println("目标用户不存在");
        }
    }
}
