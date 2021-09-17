package Service.WebSocket;

import Core.Model.RPCException;
import Core.Model.RPCType;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class WebSocketService extends Service.Abstract.Service {
    public void register(Object instance,String netName, Service.Abstract.ServiceConfig config) throws Exception {
        this.instance = instance;
        this.netName = netName;
        this.config = config;
        //反射 获取类信息=>字段、属性、方法
        StringBuilder methodId = new StringBuilder();
        for(Method method : instance.getClass().getMethods())
        {
            int modifier = method.getModifiers();
            Service.Annotation.Service annotation = method.getAnnotation(Service.Annotation.Service.class);
            if(annotation!=null){
                if(!Modifier.isInterface(modifier)){
                    methodId.append(method.getName());
                    if(annotation.parameters().length == 0){
                        for(Class<?> parameter_type : method.getParameterTypes()){
                            RPCType rpcType = config.getTypes().getTypesByType().get(parameter_type);
                            if(rpcType != null) {
                                methodId.append("-").append(rpcType.getName());
                            }
                            else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("Java中的%s类型参数尚未注册,请注意是否是泛型导致！",parameter_type.getName()));
                        }
                    }
                    else {
                        String[] types_name = annotation.parameters();
                        for(String type_name : types_name){
                            if(config.getTypes().getTypesByName().containsKey(type_name)){
                                methodId.append("-").append(type_name);
                            }
                            else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("Java中的%s抽象类型参数尚未注册,请注意是否是泛型导致！",type_name));
                        }
                    }
                    methods.put(methodId.toString(),method);
                    methodId.setLength(0);
                }
            }
        }
    }
}
