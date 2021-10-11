package com.ethereal.client.Service.Abstract;

import com.ethereal.client.Core.Event.ExceptionEvent;
import com.ethereal.client.Core.Event.LogEvent;
import com.ethereal.client.Core.Model.AbstractType;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.TrackLog;
import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Service.Interface.IService;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;

public abstract class Service implements IService {
    protected HashMap<String,Method> methods = new HashMap<>();
    protected AbstractTypes types = new AbstractTypes();
    protected String netName;
    protected String name;
    protected ServiceConfig config;
    protected ExceptionEvent exceptionEvent = new ExceptionEvent();
    protected LogEvent logEvent = new LogEvent();

    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExceptionEvent(ExceptionEvent exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }

    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
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

    public static void register(Service service) throws TrackException {
        //反射 获取类信息=>字段、属性、方法
        StringBuilder methodId = new StringBuilder();
        for(Method method : service.getClass().getMethods())
        {
            int modifier = method.getModifiers();
            com.ethereal.client.Service.Annotation.Service annotation = method.getAnnotation(com.ethereal.client.Service.Annotation.Service.class);
            if(annotation!=null){
                if(!Modifier.isInterface(modifier)){
                    methodId.append(method.getName());
                    Parameter[] parameterInfos = method.getParameters();
                    for(Parameter parameterInfo : parameterInfos){
                        AbstractType type = service.getTypes().getTypesByType().get(parameterInfo.getParameterizedType());
                        if(type == null)type = service.getTypes().getTypesByName().get(method.getAnnotation(com.ethereal.client.Core.Annotation.AbstractType.class).abstractName());
                        if(type == null)throw new TrackException(TrackException.ErrorCode.Runtime,String.format("RPC中的%s类型参数尚未被注册！",parameterInfo.getParameterizedType()));
                        methodId.append("-").append(type.getName());
                    }
                    service.methods.put(methodId.toString(),method);
                    methodId.setLength(0);
                }
            }
        }
    }
}
