package Core.Interface;

import Core.Model.RPCException;

public interface IExceptionEvent {
    void onException(RPCException exception);
    void onException(RPCException.ErrorCode code, String message);
}
