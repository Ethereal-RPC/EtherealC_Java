package RPCNet.Interface;

import Model.ClientResponseModel;
import Model.RPCException;
import RPCNet.NetConfig;

public interface IClientResponseReceive {
    public void ClientResponseReceive(String ip, String port, NetConfig config, ClientResponseModel request) throws RPCException;
}
