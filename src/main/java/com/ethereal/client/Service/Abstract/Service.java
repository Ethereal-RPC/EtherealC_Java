package com.ethereal.client.Service.Abstract;

import com.ethereal.client.Core.Event.ExceptionEvent;
import com.ethereal.client.Core.Event.LogEvent;
import com.ethereal.client.Core.Model.AbstractType;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.TrackLog;
import com.ethereal.client.Core.Model.AbstractTypes;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Service.Interface.IService;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public abstract class Service implements IService {
    protected HashMap<String,Method> methods = new HashMap<>();
    protected AbstractTypes types;
    protected String netName;
    protected ServiceConfig config;
    protected ExceptionEvent exceptionEvent = new ExceptionEvent();
    protected LogEvent logEvent = new LogEvent();

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

    public static void register(Service instance, String netName,AbstractTypes types, com.ethereal.client.Service.Abstract.ServiceConfig config) throws java.lang.Exception {
        if(config != null)instance.config = config;
        instance.netName = netName;
        instance.types = types;
        //反射 获取类信息=>字段、属性、方法
        StringBuilder methodId = new StringBuilder();
        for(Method method : instance.getClass().getMethods())
        {
            int modifier = method.getModifiers();
            com.ethereal.client.Service.Annotation.Service annotation = method.getAnnotation(com.ethereal.client.Service.Annotation.Service.class);
            if(annotation!=null){
                if(!Modifier.isInterface(modifier)){
                    methodId.append(method.getName());
                    if(annotation.parameters().length == 0){
                        for(Class<?> parameter_type : method.getParameterTypes()){
                            AbstractType rpcType = instance.types.getTypesByType().get(parameter_type);
                            if(rpcType != null) {
                                methodId.append("-").append(rpcType.getName());
                            }
                            else throw new TrackException(TrackException.ErrorCode.Runtime,String.format("Java中的%s类型参数尚未注册,请注意是否是泛型导致！",parameter_type.getName()));
                        }
                    }
                    else {
                        String[] types_name = annotation.parameters();
                        for(String type_name : types_name){
                            if(instance.types.getTypesByName().containsKey(type_name)){
                                methodId.append("-").append(type_name);
                            }
                            else throw new TrackException(TrackException.ErrorCode.Runtime,String.format("Java中的%s抽象类型参数尚未注册,请注意是否是泛型导致！",type_name));
                        }
                    }
                    instance.methods.put(methodId.toString(),method);
                    methodId.setLength(0);
                }
            }
        }
    }
}
