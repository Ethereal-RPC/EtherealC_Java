package NativeClient.Interface;

import Model.ClientResponseModel;

public interface ClientResponseModelDeserializeDelegate {
    ClientResponseModel Deserialize(String obj);
}
