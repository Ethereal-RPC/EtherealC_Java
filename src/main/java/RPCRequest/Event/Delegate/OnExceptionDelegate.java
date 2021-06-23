package RPCRequest.Event.Delegate;

import RPCNet.Net;
import RPCRequest.Request;

public interface OnExceptionDelegate {
    void OnException(Exception exception, Request request);
}
