package com.ethereal.client.Core.Event.Delegate;

import com.ethereal.client.Core.Model.TrackException;

public interface ExceptionEventDelegate {
    void onException(TrackException exception);
}
