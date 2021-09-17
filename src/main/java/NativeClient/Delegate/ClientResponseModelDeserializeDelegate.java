package NativeClient.Delegate;

import Core.Model.ClientResponseModel;

public interface ClientResponseModelDeserializeDelegate {
    ClientResponseModel Deserialize(String obj);
}
