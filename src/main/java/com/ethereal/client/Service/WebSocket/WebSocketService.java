package com.ethereal.client.Service.WebSocket;

import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Service.Abstract.Service;

public class WebSocketService extends Service {
    public WebSocketService(){
        config = new WebSocketServiceConfig();
    }
}
