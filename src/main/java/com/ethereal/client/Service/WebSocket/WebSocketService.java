package com.ethereal.client.Service.WebSocket;

import com.ethereal.client.Service.Abstract.Service;

public abstract class WebSocketService extends Service {
    public WebSocketService(){
        config = new WebSocketServiceConfig();
    }
}
