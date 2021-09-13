package RPCNet;

import Model.*;
import RPCNet.Event.ExceptionEvent;
import RPCNet.Event.LogEvent;
import RPCRequest.Request;
import RPCRequest.RequestCore;

import java.util.HashMap;

public class NetCore {

    private static HashMap<String, Net> nets = new HashMap();


    public static Net get(String name)
    {
        return nets.get(name);
    }


    public static Net register(String name) throws RPCException {
        return register(name, new NetConfig());
    }
    public static Net register(String name, NetConfig config) throws RPCException {
        Net net = nets.get(name);
        if (net == null)
        {
            net = new Net();
            net.setConfig(config);
            net.setName(name);
            nets.put(name, net);
            return net;
        }
        else return null;
    }

    public static Boolean unregister(String name)  {
        Net net = get(name);
        return unregister(net);
    }
    public static Boolean unregister(Net net)
    {
        if(net != null){
            if(nets.containsKey(net.getName())){
                //清除请求上的连接
                for(Object request : net.getRequests().values()){
                    ((Request)request).getClient().disConnect();
                }
                net.getRequests().clear();
                net.getServices().clear();
                NetConfig config = null;
                nets.remove(net.getName());
            }
        }
        return true;
    }

}
