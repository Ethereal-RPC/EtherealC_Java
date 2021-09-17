package RPCService.Abstract;

import Core.Event.ExceptionEvent;
import Core.Event.LogEvent;
import Core.Model.RPCException;
import Core.Model.RPCLog;
import Core.Model.RPCTypeConfig;
import RPCService.Interface.IService;
import RPCService.ServiceConfig;

import java.lang.reflect.Method;
import java.util.HashMap;

public abstract class Service implements IService {
    protected HashMap<String,Method> methods = new HashMap<>();
    protected RPCTypeConfig types;
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

    public RPCTypeConfig getTypes() {
        return types;
    }

    public void setTypes(RPCTypeConfig types) {
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

    public void onException(RPCException.ErrorCode code, String message) {
        onException(new RPCException(code,message));
    }
    @Override

    public void onException(RPCException exception){
        exceptionEvent.onEvent(exception);
    }
    @Override

    public void onLog(RPCLog.LogCode code, String message){
        onLog(new RPCLog(code,message));
    }
    @Override

    public void onLog(RPCLog log){
        logEvent.onEvent(log);
    }
}
