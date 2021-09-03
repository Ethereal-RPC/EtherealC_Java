package NativeClient;

import Model.RPCException;
import Model.RPCLog;
import NativeClient.Event.Delegate.OnConnectSuccessDelegate;
import NativeClient.Event.Delegate.OnLogDelegate;
import RPCNet.Net;
import RPCNet.NetCore;
import RPCRequest.Request;
import RPCRequest.RequestConfig;
import RPCRequest.RequestCore;
import org.javatuples.Pair;

public class ClientCore {

    public static SocketClient get(String netName,String serviceName)  {
        Net net = NetCore.get(netName);//获取对应的网络节点
        if(net != null){
            return get(net,serviceName);
        }
        else return null;
    }

    public static SocketClient get(Net net,String serviceName)  {
        Request request = RequestCore.getRequest(net,serviceName);
        if(request != null){
            return  request.getClient();
        }
        else return null;
    }

    public static SocketClient register(Net net,String serviceName,String host, String port) throws RPCException {
        Request request = RequestCore.getRequest(net,serviceName);
        if(request != null){
            return register(request,host,port,new ClientConfig());
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s-%s 未找到！", net.getName(),serviceName));
    }

    public static SocketClient register(Net net,String serviceName,String host, String port, ClientConfig config) throws RPCException {
        Request request = RequestCore.getRequest(net,serviceName);
        if(request != null){
            return register(request,host,port,config);
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s-%s 未找到！", net.getName(),serviceName));
    }
    public static SocketClient register(Request request,String host, String port) throws RPCException {
        return register(request,host,port,new ClientConfig());
    }

    public static SocketClient register(Request request,String host, String port, ClientConfig config) throws RPCException {
        Pair<String,String> key = new Pair<>(host,port);//二元值
        SocketClient socketClient = null;
        if(request != null){
            socketClient = request.getClient();
            if(socketClient == null){
                socketClient = new SocketClient(request.getNetName(),request.getName(),key,config);
                request.setClient(socketClient);
                socketClient.getLogEvent().register(request::OnClientLog);//日志系统
                socketClient.getExceptionEvent().register(request::OnClientException);//异常系统
                socketClient.getConnectSuccessEvent().register(client -> {
                    Request _request = RequestCore.getRequest(client.getNetName(), client.getServiceName());
                    if(_request!=null)
                    {
                        //tip:特意创建一个线程，调用连接成功的方法，防止里面有线程阻塞的函数造成连接体处于阻塞状态
                        new Thread(request::onConnectSuccess).start();
                    }
                });
            }
            return socketClient;
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s-%s 未找到！",request.getNetName(),request.getName()));
    }

    public static boolean unregister(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        if(net!=null){
            return unregister(net,serviceName);
        }
        else return true;
    }

    public static boolean unregister(Net net,String serviceName)  {
        Request request = RequestCore.getRequest(net,serviceName);
        if(request != null){
            SocketClient echoClient= request.getClient();
            if(echoClient != null){
                echoClient.disconnect();
                request.setClient(null);
                return true;
            }
        }
        return true;
    }
}
