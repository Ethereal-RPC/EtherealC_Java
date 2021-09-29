package com.ethereal.client.Client.Delegate;

import com.ethereal.client.Core.Model.ServerRequestModel;

public interface ServerRequestModelDeserializeDelegate {
    ServerRequestModel Deserialize(String obj);
}
