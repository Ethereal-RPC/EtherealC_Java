package NativeClient;

import Model.RPCException;
import RPCNet.Net;
import RPCNet.NetCore;
import RPCRequest.Request;
import RPCRequest.RequestCore;
import org.javatuples.Pair;

public class ClientCore {

    public static Client get(String netName, String serviceName)  {
        Net net = NetCore.get(netName);//获取对应的网络节点
        if(net != null){
            return get(net,serviceName);
        }
        else return null;
    }

    public static Client get(Net net, String serviceName)  {
        Request request = RequestCore.getRequest(net,serviceName);
        if(request != null){
            return  request.getClient();
        }
        else return null;
    }

    public static Client register(Net net, String serviceName, String prefixes) throws RPCException {
        Request request = RequestCore.getRequest(net,serviceName);
        if(request != null){
            return register(request,prefixes,new ClientConfig());
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s-%s 未找到！", net.getName(),serviceName));
    }

    public static Client register(Net net, String serviceName,String prefixes, ClientConfig config) throws RPCException {
        Request request = RequestCore.getRequest(net,serviceName);
        if(request != null){
            return register(request,prefixes,config);
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s-%s 未找到！", net.getName(),serviceName));
    }
    public static Client register(Request request, String prefixes) throws RPCException {
        return register(request,prefixes,new ClientConfig());
    }

    public static Client register(Request request, String prefixes, ClientConfig config) throws RPCException {
        Client socketClient = null;
        if(request != null){
            socketClient = request.getClient();
            if(socketClient == null){
                socketClient = new Client(request.getNetName(),request.getName(),prefixes,config);
                request.setClient(socketClient);
                socketClient.getLogEvent().register(request::OnClientLog);//日志系统
                socketClient.getExceptionEvent().register(request::OnClientException);//异常系统
                socketClient.getConnectEvent().register(client -> {
                    Request _request = RequestCore.getRequest(client.getNetName(), client.getServiceName());
                    if(_request!=null)
                    {
                        _request.onConnectSuccess();
                    }
                });
            }
            return socketClient;
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s-%s 未找到！",request.getNetName(),request.getName()));
    }

    public static boolean unregister(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        return unregister(net,serviceName);
    }

    public static boolean unregister(Net net,String serviceName)  {
        if(net!=null){
            Request request = RequestCore.getRequest(net,serviceName);
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
