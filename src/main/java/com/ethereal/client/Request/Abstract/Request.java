package com.ethereal.client.Request.Abstract;

import com.ethereal.client.Core.Event.ExceptionEvent;
import com.ethereal.client.Core.Event.LogEvent;
import com.ethereal.client.Core.Model.ClientRequestModel;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.TrackLog;
import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Request.Event.ConnectSuccessEvent;
import com.ethereal.client.Request.Interface.IRequest;
import net.sf.cglib.proxy.*;

import java.util.concurrent.ConcurrentHashMap;

public abstract class Request implements IRequest {
    protected final ConcurrentHashMap<Integer,ClientRequestModel> tasks = new ConcurrentHashMap<>();
    protected String name;
    protected String netName;
    protected RequestConfig config;
    protected Client client;//连接体
    protected ExceptionEvent exceptionEvent = new ExceptionEvent();
    protected LogEvent logEvent = new LogEvent();
    public static Request register(Class<Request> instance_class, String netName, String serviceName, com.ethereal.client.Request.Abstract.RequestConfig config){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(instance_class);
        RequestMethodInterceptor  interceptor = new RequestMethodInterceptor();
        Callback noOp= NoOp.INSTANCE;
        enhancer.setCallbacks(new Callback[]{noOp,interceptor});
        enhancer.setCallbackFilter(method -> {
            if(method.getAnnotation(com.ethereal.client.Request.Annotation.Request.class) != null){
                return 1;
            }
            else return 0;
        });
        Request instance = (Request)enhancer.create();
        interceptor.setInstance(instance);
        instance.setName(serviceName);
        instance.setNetName(netName);
        instance.setConfig(config);
        return instance;
    }

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
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(TrackLog.LogCode code, String message){
        onLog(new TrackLog(code,message));
    }
    @Override
    public void onLog(TrackLog log){
        logEvent.onEvent(log);
    }

    public void onConnectSuccess(){
        connectSuccessEvent.onEvent(this);
    }
}
