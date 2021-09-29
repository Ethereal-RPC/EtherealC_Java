package com.ethereal.client.Service.Delegate;

import com.ethereal.client.Core.Model.ClientResponseModel;
import com.ethereal.client.Core.Model.TrackException;

public interface IClientResponseReceive {
    public void ClientResponseReceive(ClientResponseModel request) throws TrackException;
}
