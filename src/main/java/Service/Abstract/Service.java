package Service.Abstract;

import Core.Event.ExceptionEvent;
import Core.Event.LogEvent;
import Core.Model.TrackException;
import Core.Model.TrackLog;
import Core.Model.AbstractTypes;
import Service.Interface.IService;

import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class Service implements IService {
    protected HashMap<String,Method> methods = new HashMap<>();
    protected AbstractTypes types;
    protected Object instance = null;
    protected String netName;
    protected ServiceConfig config;

    protected ExceptionEvent exceptionEvent = new ExceptionEvent();
    protected LogEvent logEvent = new LogEvent();

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public AbstractTypes getTypes() {
        return types;
    }

    public void setTypes(AbstractTypes types) {
        this.types = types;
    }

    public HashMap<String, Method> getMethods() {
        return methods;
    }
    public void setMethods(HashMap<String, Method> methods) {
        this.methods = methods;
    }

    public ServiceConfig getConfig() {
        return config;
    }

    public void setConfig(ServiceConfig config) {
        this.config = config;
    }


    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }
    @Override

    public void onException(TrackException.ErrorCode code, String message) {
        onException(new TrackException(code,message));
    }
    @Override

    public void onException(TrackException exception){
        exception.setService(this);
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(TrackLog.LogCode code, String message){
        onLog(new TrackLog(code,message));
    }
    @Override

    public void onLog(TrackLog log){
        log.setService(this);
        logEvent.onEvent(log);
    }
}
