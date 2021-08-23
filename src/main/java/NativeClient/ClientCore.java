package NativeClient;

import Model.RPCException;
import Model.RPCLog;
import NativeClient.Event.Delegate.OnLogDelegate;
import RPCNet.Net;
import RPCNet.NetCore;
import RPCRequest.Request;
import RPCRequest.RequestConfig;
import RPCRequest.RequestCore;
import org.javatuples.Pair;

public class ClientCore {

    public static SocketClient get(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        if(net != null){
            return get(net,serviceName);
        }
        else return null;
    }

    public static SocketClient get(Net net,String serviceName)  {
        Request request = (Request) net.getRequests().get(serviceName);
        if(request != null){
            return  request.getClient();
        }
        else return null;
    }

    public static SocketClient register(Net net,String serviceName,String host, String port) throws RPCException {
        Request request = (Request) net.getRequests().get(serviceName);
        if(request != null){
            return register(request,serviceName,host,port,new ClientConfig());
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s-%s 未找到！", net.getName(),serviceName));
    }

    public static SocketClient register(Net net,String serviceName,String host, String port, ClientConfig config) throws RPCException {
        Request request = (Request) net.getRequests().get(serviceName);
        if(request != null){
            return register(request,serviceName,host,port,config);
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s-%s 未找到！", net.getName(),serviceName));
    }
    public static SocketClient register(Request request,String host, String port) throws RPCException {
        return register(request,host,port);
    }

    public static SocketClient register(Request request,String serviceName,String host, String port, ClientConfig config) throws RPCException {
        Pair<String,String> key = new Pair<>(host,port);
        SocketClient socketClient = null;
        if(request != null){
            socketClient = request.getClient();
            if(socketClient == null){
                socketClient = new SocketClient(request.getName(),request.getNetName(),key,config);
                request.setClient(socketClient);
                SocketClient finalSocketClient = socketClient;
                socketClient.getLogEvent().register(request::OnClientLog);
                socketClient.getExceptionEvent().register(request::OnClientException);
            }
            return socketClient;
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s-%s 未找到！",request.getName(),serviceName));
    }

    public static boolean unregister(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        if(net!=null){
            return unregister(net,serviceName);
        }
        else return true;
    }

    public static boolean unregister(Net net,String serviceName)  {
        Request request = RequestCore.get(net,serviceName);
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
