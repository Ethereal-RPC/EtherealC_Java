package com.ethereal.client.Core.Interface;

import com.ethereal.client.Core.Model.TrackLog;

public interface ILogEvent {
    void onLog(TrackLog log);
    void onLog(TrackLog.LogCode code, String message);
}
