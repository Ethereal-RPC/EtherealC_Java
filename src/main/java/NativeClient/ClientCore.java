package NativeClient;

import Model.ClientRequestModel;
import NativeClient.Interface.IConnectSuccess;
import RPCNet.Interface.IClientRequestSend;
import RPCNet.NetConfig;
import RPCNet.NetCore;
import org.javatuples.Pair;

import java.util.concurrent.ConcurrentHashMap;

public class ClientCore {
    private static ConcurrentHashMap<Pair<String,String>, SocketClient> clients = new ConcurrentHashMap<>();

    public static SocketClient startClient(String host, String port){
        return startClient(host,port,new ClientConfig());
    }

    public static SocketClient startClient(String host, String port, ClientConfig config){
        Pair<String,String> key = new Pair<>(host,port);
        SocketClient socketClient = null;
        socketClient = clients.get(key);
        if(socketClient == null){
            NetConfig netConfig = NetCore.Get(key);
            if(netConfig != null){
                socketClient = new SocketClient(key,config);
                clients.put(key, socketClient);
                SocketClient finalSocketClient = socketClient;
                netConfig.setClientRequestSend(new IClientRequestSend() {
                    @Override
                    public void ClientRequestSend(ClientRequestModel request) {
                        finalSocketClient.send(request);
                    }
                });
            }
        }
        return socketClient;
    }
    public static SocketClient getClient(String ip, String port){
        Pair<String,String> key = new Pair<>(ip,port);
        return clients.get(key);
    }
    public static void destory(String ip,String port){
        Pair<String,String> key = new Pair<>(ip,port);
        SocketClient echoClient;
        echoClient = clients.get(key);
        if(echoClient != null){
            echoClient.disconnect();
            clients.remove(key);
        }
    }
}
