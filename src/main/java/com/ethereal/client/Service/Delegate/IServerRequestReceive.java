package com.ethereal.client.Service.Delegate;

import com.ethereal.client.Core.Model.ServerRequestModel;

public interface IServerRequestReceive {
    public void ServerRequestReceive(ServerRequestModel request) throws Exception;
}
