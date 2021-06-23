package RPCService.Event.Delegate;

import Model.RPCLog;
import RPCRequest.Request;
import RPCService.Service;

public interface OnLogDelegate {
    void OnLog(RPCLog log, Service service);
}
