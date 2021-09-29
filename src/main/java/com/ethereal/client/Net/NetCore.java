package com.ethereal.client.Net;

import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.Abstract.NetConfig;
import com.ethereal.client.Net.WebSocket.WebSocketNet;
import com.ethereal.client.Net.WebSocket.WebSocketNetConfig;
import com.ethereal.client.Request.Abstract.Request;

import java.util.HashMap;

public class NetCore {

    private static HashMap<String, Net> nets = new HashMap();


    public static Net get(String name)
    {
        return nets.get(name);
    }


    public static Net register(String name,NetType netType) throws TrackException {
        if(netType == NetType.WebSocket){
            return register(name,new WebSocketNetConfig(),netType);
        }
        else throw new TrackException(TrackException.ErrorCode.Core, String.format("未有针对%s的Net-Register处理",netType));
    }
    public static Net register(String name, NetConfig config, NetType netType) throws TrackException {
        Net net = nets.get(name);
        if (net == null)
        {
            if(netType == NetType.WebSocket){
                net = new WebSocketNet();
            }
            else throw new TrackException(TrackException.ErrorCode.Core, String.format("未有针对%s的Net-Register处理",netType));
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
