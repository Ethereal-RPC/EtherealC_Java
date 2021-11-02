package com.ethereal.client.Request.WebSocket;

import com.ethereal.client.Request.Abstract.Request;

public abstract class WebSocketRequest extends Request {
    public WebSocketRequest() {
        config = new WebSocketRequestConfig();
    }
}
