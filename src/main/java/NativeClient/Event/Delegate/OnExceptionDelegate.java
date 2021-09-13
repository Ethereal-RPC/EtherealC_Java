package NativeClient.Event.Delegate;

import NativeClient.Client;

public interface OnExceptionDelegate {
    void OnException(Exception exception, Client client);
}
