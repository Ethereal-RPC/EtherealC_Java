package com.ethereal.client.Net.WebSocket;

import com.ethereal.client.Client.Event.Delegate.OnConnectFailDelegate;
import com.ethereal.client.Client.WebSocket.WebSocketClient;
import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Client.Abstract.ClientConfig;
import com.ethereal.client.Client.ClientCore;
import com.ethereal.client.Client.Event.Delegate.OnDisConnectDelegate;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Request.RequestCore;
import com.ethereal.client.Service.Abstract.Service;
import com.ethereal.client.Service.ServiceCore;
import com.ethereal.client.Utils.AutoResetEvent;
import org.javatuples.Pair;

import java.util.concurrent.TimeUnit;

public class WebSocketNet extends Net {
    public WebSocketNetConfig getConfig() {
        return (WebSocketNetConfig)config;
    }
    AutoResetEvent netNodeSign = new AutoResetEvent(false);


    public WebSocketNet(String name){
        super(name);
        netType = NetType.WebSocket;
        config = new WebSocketNetConfig();
    }
    @Override
    public boolean publish() throws java.lang.Exception {
        try {
            if(client!=null){
                client.connect();
            }
        }
        catch (Exception e){
            onException(new TrackException(e));
        }
        return true;
    }
}
