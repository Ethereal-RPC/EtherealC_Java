package Core.Event.Delegate;

import Core.Model.RPCException;

public interface ExceptionEventDelegate {
    void onException(RPCException exception);
}
