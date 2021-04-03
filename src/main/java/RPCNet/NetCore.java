package RPCNet;

import Model.ClientResponseModel;
import Model.RPCException;
import Model.ServerRequestModel;
import RPCNet.Interface.IClientResponseReceive;
import RPCNet.Interface.IServerRequestReceive;
import RPCRequest.Request;
import RPCRequest.RequestCore;
import RPCService.ServiceCore;
import org.javatuples.Pair;
import org.javatuples.Tuple;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class NetCore {
    private static HashMap<Pair<String, String>, NetConfig> configs = new HashMap();

    public static NetConfig Get(Pair<String, String> key)
    {
        return configs.get(key);
    }
    public static void Register(String ip, String port) throws RPCException {
        Register(ip, port, new NetConfig());
    }
    public static void Register(String ip, String port, NetConfig config) throws RPCException {
        NetConfig value = configs.get(new Pair<>(ip, port));
        if (value == null)
        {
            config.setServerRequestReceive(new IServerRequestReceive() {
                @Override
                public void ServerRequestReceive(String ip, String port, NetConfig config, ServerRequestModel request) throws IllegalAccessException, RPCException, InvocationTargetException {
                    ServiceCore.ServerRequestReceive(ip,port,config,request);
                }
            });
            config.setClientResponseReceive(new IClientResponseReceive() {
                @Override
                public void ClientResponseReceive(String ip, String port, NetConfig config, ClientResponseModel request) throws RPCException {
                    RequestCore.ClientResponseProcess(ip,port,config,request);
                }
            });
            configs.put(new Pair<>(ip, port), config);
        }
        else throw new RPCException(RPCException.ErrorCode.RegisterError,String.format("%s-%s的NetConfig已经注册",ip,port));
    }
    public static Boolean unRegister(String ip, String port)
    {
        NetConfig config = null;
        return configs.remove(new Pair<String, String>(ip, port),config);
    }
}
