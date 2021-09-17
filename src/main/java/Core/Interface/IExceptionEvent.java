package Core.Interface;

import Core.Model.TrackException;

public interface IExceptionEvent {
    void onException(TrackException exception);
    void onException(TrackException.ErrorCode code, String message);
}
