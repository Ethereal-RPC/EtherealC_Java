package RPCRequest.Event.Delegate;

import Model.RPCLog;
import RPCNet.Net;
import RPCRequest.Request;

public interface OnLogDelegate {
    void OnLog(RPCLog log, Request request);
}
