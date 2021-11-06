package com.ethereal.client.Client;

import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.RequestCore;

public class ClientCore {

    public static Client get(String netName)  {
        Net net = NetCore.get(netName);//获取对应的网络节点
        if(net != null){
            return net.getClient();
        }
        else return null;
    }

    public static Client register(Net net, Client client) throws TrackException {
        if(net.getClient() == null){
            net.setClient(client);
            client.setNet(net);
            client.getLogEvent().register(net::onLog);//日志系统
            client.getExceptionEvent().register(net::onException);//异常系统
            client.getConnectSuccessEvent().register(value -> {
                for(Request request:value.getNet().getRequests().values())
                {
                    request.onConnectSuccess();
                }
            });
        }
        return client;
    }

    public static boolean unregister(Client client)  {
        client.getNet().setClient(null);
        client.setNet(null);
        client.disConnect();
        return true;
    }
}
