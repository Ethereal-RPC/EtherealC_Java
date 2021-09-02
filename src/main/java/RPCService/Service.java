package RPCService;

import Model.RPCException;
import Model.RPCLog;
import Model.RPCType;
import Model.RPCTypeConfig;
import RPCService.Event.ExceptionEvent;
import RPCService.Event.LogEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class Service {
    private HashMap<String,Method> methods = new HashMap<>();
    private RPCTypeConfig types;
    private Object instance = null;
    private String netName;
    private ServiceConfig config;

    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();

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

    public void register(Object instance,String netName, ServiceConfig config) throws Exception {
        this.instance = instance;
        this.netName = netName;
        this.config = config;
        //反射 获取类信息=>字段、属性、方法
        StringBuilder methodId = new StringBuilder();
        for(Method method : instance.getClass().getMethods())
        {
            int modifier = method.getModifiers();
            Annotation.RPCService annotation = method.getAnnotation(Annotation.RPCService.class);
            if(annotation!=null){
                if(!Modifier.isInterface(modifier)){
                    methodId.append(method.getName());
                    if(annotation.parameters().length == 0){
                        String type_name;
                        for(Class<?> parameter_type : method.getParameterTypes()){
                            RPCType rpcType = config.getTypes().getTypesByType().get(parameter_type);
                            if(rpcType != null) {
                                methodId.append("-").append(rpcType.getName());
                            }
                            else onException(new RPCException(RPCException.ErrorCode.Runtime,String.format("Java中的%s类型参数尚未注册,请注意是否是泛型导致！",parameter_type.getName())));
                        }
                    }
                    else {
                        String[] types_name = annotation.parameters();
                        for(String type_name : types_name){
                            if(config.getTypes().getTypesByName().containsKey(type_name)){
                                methodId.append("-").append(type_name);
                            }
                            else onException(new RPCException(RPCException.ErrorCode.Runtime,String.format("Java中的%s抽象类型参数尚未注册,请注意是否是泛型导致！",type_name)));
                        }
                    }
                    methods.put(methodId.toString(),method);
                    methodId.setLength(0);
                }
            }
        }
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
    public void onException(RPCException.ErrorCode code, String message) throws Exception {
        onException(new RPCException(code,message));
    }
    public void onException(Exception exception) throws Exception {
        exceptionEvent.onEvent(exception,this);
        throw exception;
    }

    public void onLog(RPCLog.LogCode code, String message){
        onLog(new RPCLog(code,message));
    }
    public void onLog(RPCLog log){
        logEvent.onEvent(log,this);
    }
}
