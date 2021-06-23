package NativeClient.Event.Delegate;

import Model.RPCLog;
import NativeClient.SocketClient;

public interface OnLogDelegate {
    void OnLog(RPCLog log, SocketClient client);
}
