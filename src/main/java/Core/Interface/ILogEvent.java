package Core.Interface;

import Core.Model.RPCLog;

public interface ILogEvent {
    void onLog(RPCLog log);
    void onLog(RPCLog.LogCode code, String message);
}
