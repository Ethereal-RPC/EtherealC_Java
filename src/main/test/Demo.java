import com.ethereal.client.Client.WebSocket.WebSocketClient;
import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Client.ClientCore;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Net.WebSocket.WebSocketNet;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.EventRegister.Delegate.OnConnectSuccessDelegate;
import com.ethereal.client.Request.RequestCore;
import com.ethereal.client.Service.ServiceCore;
import RequestDemo.ServerRequest;
import ServiceDemo.ClientService;

import java.util.Scanner;

public class Demo {
    public static void main(String[] args) throws Exception {
        single();
    }
    public static void single() throws Exception {
        Scanner scanner = new Scanner(System.in);
        String  port = "28015";
        System.out.println("请选择端口(0-3):");
        port = scanner.next();
        switch (port){
            case "0":
                port = "28015";
                break;
            case "1":
                port = "28016";
                break;
            case "2":
                port = "28017";
                break;
            case "3":
                port = "28018";
                break;
        }
        Net net = NetCore.register(new WebSocketNet("demo"));
        net.getExceptionEvent().register(exception -> System.out.println(exception.getException().getMessage()));
        net.getLogEvent().register(log -> System.out.println(log.getMessage()));
        //向网关注册请求
        ServerRequest serverRequest = RequestCore.register(net,ServerRequest.class);
        //向网关注册服务
        ClientService service = ServiceCore.register(serverRequest,new ClientService());
        Client socketClient = new WebSocketClient("ethereal://127.0.0.1:28015/NetDemo/".replace("28015",port));
        socketClient.getConnectSuccessEvent().register(client -> System.out.println("Single启动成功"));
        socketClient.getDisConnectEvent().register(client -> System.out.println("Single启动失败"));
        ClientCore.register(serverRequest,socketClient);
        serverRequest.getConnectSuccessEvent().register(new OnConnectSuccessDelegate() {
            @Override
            public void OnConnectSuccess(Request request) {
                ((ServerRequest)request).test("asd",123,12);
            }
        });
    }
}
