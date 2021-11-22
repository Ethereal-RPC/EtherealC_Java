package com.ethereal.client.Client;

import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.RequestCore;

public class ClientCore {

    public static Client get(String net_name,String request_name)  {
        Net net = NetCore.get(net_name);//获取对应的网络节点
        if(net != null){
            Request request = RequestCore.get(net,request_name);//获取对应的网络节点
            if(request!=null){
                return request.getClient();
            }
        }
        return null;
    }

    public static Client register(Request request, Client client) throws TrackException {
        request.setClient(client);
        client.setRequest(request);
        client.getLogEvent().register(request::onLog);//日志系统
        client.getExceptionEvent().register(request::onException);//异常系统
        client.getConnectSuccessEvent().register(value -> {
            request.onConnectSuccess();
        });
        return client;
    }

    public static boolean unregister(Client client)  {
        client.getRequest().setClient(null);
        client.setRequest(null);
        client.disConnect();
        return true;
    }
}
