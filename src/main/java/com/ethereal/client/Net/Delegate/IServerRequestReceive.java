package com.ethereal.client.Net.Delegate;

import com.ethereal.client.Core.Model.ServerRequestModel;

public interface IServerRequestReceive {
    public void ServerRequestReceive(ServerRequestModel request) throws Exception;
}
