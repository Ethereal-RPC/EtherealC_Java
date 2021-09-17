package NativeClient.Delegate;

import Core.Model.ServerRequestModel;

public interface ServerRequestModelDeserializeDelegate {
    ServerRequestModel Deserialize(String obj);
}
