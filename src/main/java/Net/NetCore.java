package Net;

import Core.Enums.NetType;
import Core.Model.RPCException;
import Net.Abstract.Net;
import Net.Abstract.NetConfig;
import Net.WebSocket.WebSocketNet;
import Net.WebSocket.WebSocketNetConfig;
import Request.Abstract.Request;

import java.util.HashMap;

public class NetCore {

    private static HashMap<String, Net> nets = new HashMap();


    public static Net get(String name)
    {
        return nets.get(name);
    }


    public static Net register(String name,NetType netType) throws RPCException {
        if(netType == NetType.WebSocket){
            return register(name,new WebSocketNetConfig(),netType);
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("未有针对%s的Net-Register处理",netType));
    }
    public static Net register(String name, NetConfig config, NetType netType) throws RPCException {
        Net net = nets.get(name);
        if (net == null)
        {
            if(netType == NetType.WebSocket){
                net = new WebSocketNet();
            }
            else throw new RPCException(RPCException.ErrorCode.Core, String.format("未有针对%s的Net-Register处理",netType));
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
