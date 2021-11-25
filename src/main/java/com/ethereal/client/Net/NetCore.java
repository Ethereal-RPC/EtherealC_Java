package com.ethereal.client.Net;

import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.Abstract.NetConfig;
import com.ethereal.client.Net.WebSocket.WebSocketNet;
import com.ethereal.client.Net.WebSocket.WebSocketNetConfig;
import com.ethereal.client.Request.Abstract.Request;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class NetCore {

    private static final HashMap<String, Net> nets = new HashMap();


    public static Net get(String name)
    {
        return nets.get(name);
    }

    public static Net register(Net net) throws TrackException {
        if (!net.isRegister())
        {
            net.setRegister(true);
            nets.put(net.getName(), net);
            return net;
        }
        else throw new TrackException(TrackException.ErrorCode.Core, String.format("Net:%s 已注册", net.getName()));
    }

    public static Boolean unregister(String name) throws TrackException {
        Net net = get(name);
        return unregister(net);
    }
    public static Boolean unregister(Net net) throws TrackException {
        if(net.isRegister()){
            if(net != null){
                nets.remove(net.getName());
            }
            net.setRegister(false);
            return true;
        }
        else throw new TrackException(TrackException.ErrorCode.Core, String.format("Net:%s 已注册", net.getName()));
    }

}
