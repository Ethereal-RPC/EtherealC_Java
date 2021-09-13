package NativeClient.Event.Delegate;

import Model.RPCException;
import NativeClient.Client;

public interface OnDisConnectDelegate {
    void OnDisConnect(Client client) ;
}
