package com.ethereal.client.Core.EventRegister.Delegate;

import com.ethereal.client.Core.Model.TrackException;

public interface ExceptionEventDelegate {
    void onException(TrackException exception);
}
