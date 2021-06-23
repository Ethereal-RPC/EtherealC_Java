package NativeClient.Interface;

import Model.ServerRequestModel;

public interface ServerRequestModelDeserializeDelegate {
    ServerRequestModel Deserialize(String obj);
}
