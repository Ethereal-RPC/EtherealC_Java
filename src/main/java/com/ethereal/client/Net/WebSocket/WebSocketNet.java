package com.ethereal.client.Net.WebSocket;

import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Utils.AutoResetEvent;

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
}
