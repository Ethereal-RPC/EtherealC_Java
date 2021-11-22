package com.ethereal.client.Core.EventRegister.Delegate;

import com.ethereal.client.Core.Model.TrackLog;

public interface LogEventDelegate {
    void onLog(TrackLog log);
}
