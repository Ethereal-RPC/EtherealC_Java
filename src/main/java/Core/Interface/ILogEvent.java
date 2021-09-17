package Core.Interface;

import Core.Model.TrackLog;

public interface ILogEvent {
    void onLog(TrackLog log);
    void onLog(TrackLog.LogCode code, String message);
}
