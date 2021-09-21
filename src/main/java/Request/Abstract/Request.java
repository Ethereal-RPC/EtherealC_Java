package Request.Abstract;

import Core.Event.ExceptionEvent;
import Core.Event.LogEvent;
import Core.Model.ClientRequestModel;
import Core.Model.TrackException;
import Core.Model.TrackLog;
import Client.Abstract.Client;
import Request.Event.ConnectSuccessEvent;
import Request.Interface.IRequest;

import java.lang.reflect.InvocationHandler;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Request implements IRequest,InvocationHandler {
    protected final ConcurrentHashMap<Integer,ClientRequestModel> tasks = new ConcurrentHashMap<>();
    protected String name;
    protected String netName;
    protected RequestConfig config;
    protected Client client;//连接体
    protected ExceptionEvent exceptionEvent = new ExceptionEvent();
    protected LogEvent logEvent = new LogEvent();
    //连接成功事件
    protected ConnectSuccessEvent connectSuccessEvent = new ConnectSuccessEvent();

    public ConnectSuccessEvent getConnectSuccessEvent() {
        return connectSuccessEvent;
    }

    public void setConnectSuccessEvent(ConnectSuccessEvent connectSuccessEvent) {
        this.connectSuccessEvent = connectSuccessEvent;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public RequestConfig getConfig() {
        return config;
    }
    public void setConfig(RequestConfig config) {
        this.config = config;
    }
    public ConcurrentHashMap<Integer, ClientRequestModel> getTasks() {
        return tasks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
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
        exception.setRequest(this);
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(TrackLog.LogCode code, String message){
        onLog(new TrackLog(code,message));
    }
    @Override
    public void onLog(TrackLog log){
        log.setRequest(this);
        logEvent.onEvent(log);
    }

    public void onConnectSuccess(){
        connectSuccessEvent.onEvent(this);
    }
}
