package NativeClient.Event.Delegate;

import NativeClient.SocketClient;

public interface OnExceptionDelegate {
    void OnException(Exception exception, SocketClient client);
}
