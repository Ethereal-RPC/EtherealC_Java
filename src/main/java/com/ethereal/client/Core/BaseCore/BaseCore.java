package com.ethereal.client.Core.BaseCore;

import com.ethereal.client.Core.EventRegister.ExceptionEvent;
import com.ethereal.client.Core.EventRegister.LogEvent;
import com.ethereal.client.Core.Interface.IExceptionEvent;
import com.ethereal.client.Core.Interface.ILogEvent;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.TrackLog;

public class BaseCore implements IExceptionEvent, ILogEvent {
    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();
    private Boolean isRegister;

    public Boolean isRegister() {
        return isRegister;
    }

    public void setRegister(Boolean register) {
        isRegister = register;
    }

    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public void setExceptionEvent(ExceptionEvent exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }

    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
    }

    @Override

    public void onException(TrackException.ErrorCode code, String message) {
        onException(new TrackException(code,message));
    }
    @Override
    public void onException(TrackException exception)  {
        exception.setSender(this);
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(TrackLog.LogCode code, String message){
        onLog(new TrackLog(code,message));
    }
    @Override
    public void onLog(TrackLog log){
        log.setSender(this);
        logEvent.onEvent(log);
    }

}
