package RPCRequest.Delegate;

import Core.Model.ClientResponseModel;
import Core.Model.RPCException;

public interface IClientResponseReceive {
    public void ClientResponseReceive(ClientResponseModel request) throws RPCException;
}
