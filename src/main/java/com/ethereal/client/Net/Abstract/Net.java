package com.ethereal.client.Net.Abstract;

import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.EventRegister.ExceptionEvent;
import com.ethereal.client.Core.EventRegister.LogEvent;
import com.ethereal.client.Core.Model.*;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Net.Interface.INet;
import com.ethereal.client.Request.Abstract.Request;

import java.util.HashMap;

public abstract class Net implements INet {
    protected NetConfig config;
    protected String name;
    protected NetType netType;
    protected ExceptionEvent exceptionEvent = new ExceptionEvent();
    protected LogEvent logEvent = new LogEvent();
    //Java没有自带三元组，这里就引用Kotlin了.
    protected HashMap<String, Request> requests = new HashMap<>();


    public Net(String name){
        this.name = name;
    }
    public NetType getNetType() {
        return netType;
    }

    public void setNetType(NetType netType) {
        this.netType = netType;
    }

    public void setExceptionEvent(ExceptionEvent exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }

    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public NetConfig getConfig() {
        return config;
    }

    public void setConfig(NetConfig config) {
        this.config = config;
    }
    public HashMap<String, Request> getRequests() {
        return requests;
    }

    public void setRequests(HashMap<String, Request> requests) {
        this.requests = requests;
    }

    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }
    @Override

    public void onException(TrackException.ErrorCode code, String message){
        onException(new TrackException(code,message));
    }
    @Override
    public void onException(TrackException exception)  {
        exception.setNet(this);
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(TrackLog.LogCode code, String message){
        onLog(new TrackLog(code,message));
    }
    @Override
    public void onLog(TrackLog log){
        log.setNet(this);
        logEvent.onEvent(log);
    }
}
