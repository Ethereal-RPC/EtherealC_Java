package com.ethereal.client.Client.Delegate;

import com.ethereal.client.Core.Model.ClientResponseModel;

public interface ClientResponseModelDeserializeDelegate {
    ClientResponseModel Deserialize(String obj);
}
