package com.ethereal.client.Net.NetNode.Request;

import com.ethereal.client.Request.Annotation.Request;
import com.ethereal.client.Net.NetNode.Model.NetNode;
import com.ethereal.client.Request.WebSocket.WebSocketRequest;

public class ServerNetNodeRequest extends WebSocketRequest {
    @Request
    public NetNode GetNetNode(String servicename){
        return null;
    }
}
