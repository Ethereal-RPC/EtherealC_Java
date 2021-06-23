package RPCNet;

import Model.*;

import java.util.HashMap;

public class NetCore {
    private static HashMap<String, Net> nets = new HashMap();

    public static Net get(String name)
    {
        return nets.get(name);
    }


    public static void register(String name) throws RPCException {
        register(name, new NetConfig());
    }
    public static void register(String name, NetConfig config) throws RPCException {
        Net net = nets.get(name);
        if (net == null)
        {
            net = new Net();
            net.setConfig(config);
            nets.put(name, net);
        }
        else throw new RPCException(RPCException.ErrorCode.Core,String.format("%s Net 已经注册",name));
    }

    public static Boolean unregister(String name) throws RPCException {
        Net net = get(name);
        if(net != null){
            return unregister(net);
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("%s Net未找到", name));
    }
    public static Boolean unregister(Net net)
    {
        NetConfig config = null;
        return nets.remove(net,config);
    }
}
