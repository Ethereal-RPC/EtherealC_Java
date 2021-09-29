package com.ethereal.client.Core.Interface;

import com.ethereal.client.Core.Model.TrackException;

public interface IExceptionEvent {
    void onException(TrackException exception);
    void onException(TrackException.ErrorCode code, String message);
}
