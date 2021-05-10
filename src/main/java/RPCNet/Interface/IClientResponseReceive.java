package RPCNet.Interface;

import Model.ClientResponseModel;
import Model.RPCException;
import RPCNet.NetConfig;

public interface IClientResponseReceive {
    public void ClientResponseReceive(ClientResponseModel request) throws RPCException;
}
