package NativeClient.Event.Delegate;

import NativeClient.SocketClient;

public interface OnConnectFailDelegate {
    void OnConnectFail(SocketClient client) throws Exception;
}
