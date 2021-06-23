package NativeClient;

import Model.RPCException;
import RPCNet.Net;
import RPCNet.NetCore;
import org.javatuples.Pair;

public class ClientCore {
    public static SocketClient get(String netName) throws RPCException {
        Net net = NetCore.get(netName);
        if(net != null){
            return get(net);
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s Net未找到！", netName));
    }
    public static SocketClient get(Net net) throws RPCException {
        return net.getClient();
    }
    public static SocketClient register(Net net,String host, String port){
        return register(net,host,port,new ClientConfig());
    }

    public static SocketClient register(Net net,String host, String port, ClientConfig config){
        Pair<String,String> key = new Pair<>(host,port);
        SocketClient socketClient = null;
        socketClient = net.getClient();
        if(socketClient == null){
            if(net != null){
                socketClient = new SocketClient(net.getName(),key,config);
                net.setClient(socketClient);
                SocketClient finalSocketClient = socketClient;
                net.setClientRequestSend(finalSocketClient::send);
            }
        }
        return socketClient;
    }

    public static boolean unregister(String netName){
        Net net = NetCore.get(netName);
        return unregister(net);
    }

    public static boolean unregister(Net net){
        SocketClient echoClient= net.getClient();
        if(echoClient != null){
            echoClient.disconnect();
            net.setClient(null);
            net.setClientRequestSend(null);
            return true;
        }
        return false;
    }
}
