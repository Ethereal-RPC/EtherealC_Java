package RPCNet;

import Model.*;
import RPCRequest.Request;
import RPCRequest.RequestCore;
import RPCService.Service;
import RPCService.ServiceCore;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class NetCore {
    private static HashMap<Pair<String, String>, Net> nets = new HashMap();

    public static Net Get(Pair<String, String> key)
    {
        return nets.get(key);
    }
    public static void register(String ip, String port) throws RPCException {
        register(ip, port, new NetConfig());
    }
    public static void register(String ip, String port, NetConfig config) throws RPCException {
        Net net = nets.get(new Pair<>(ip, port));
        if (net == null)
        {
            net = new Net();
            net.setConfig(config);
            nets.put(new Pair<>(ip, port), net);
        }
        else throw new RPCException(RPCException.ErrorCode.RegisterError,String.format("%s-%s的NetConfig已经注册",ip,port));
    }
    public static Boolean unregister(String ip, String port)
    {
        NetConfig config = null;
        return nets.remove(new Pair<String, String>(ip, port),config);
    }

}
