package RPCService.Event.Delegate;

import RPCRequest.Request;
import RPCService.Service;

public interface OnExceptionDelegate {
    void OnException(Exception exception, Service service) throws Exception;
}
