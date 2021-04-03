package RPCNet.Interface;

import Model.ClientRequestModel;
import Model.ServerRequestModel;
import RPCNet.NetConfig;

public interface IClientRequestSend {
    public void ClientRequestSend(ClientRequestModel request);
}
