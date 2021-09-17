package Core.Event.Delegate;

import Core.Model.TrackException;

public interface ExceptionEventDelegate {
    void onException(TrackException exception);
}
