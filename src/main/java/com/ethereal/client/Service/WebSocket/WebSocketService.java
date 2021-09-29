package com.ethereal.client.Service.WebSocket;

import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.AbstractType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class WebSocketService extends com.ethereal.client.Service.Abstract.Service {
    public WebSocketService(){
        config = new WebSocketServiceConfig();
    }
}
