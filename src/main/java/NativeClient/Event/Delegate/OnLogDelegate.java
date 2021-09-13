package NativeClient.Event.Delegate;

import Model.RPCLog;
import NativeClient.Client;

public interface OnLogDelegate {
    void OnLog(RPCLog log, Client client);
}
