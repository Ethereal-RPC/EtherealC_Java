package RPCService;

import Model.RPCException;
import Model.RPCType;
import Model.RPCTypeConfig;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class Service {
    private HashMap<String,Method> methods = new HashMap<>();
    private RPCTypeConfig types;
    private Object instance = null;

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

    public void register(Object instance, RPCTypeConfig type) throws RPCException {
        this.instance = instance;
        StringBuilder methodId = new StringBuilder();
        this.types = type;
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
                            RPCType rpcType = type.getTypesByType().get(parameter_type);
                            if(rpcType != null) {
                                methodId.append("-").append(rpcType.getName());
                            }
                            else throw new RPCException(String.format("Java中的%s类型参数尚未注册,请注意是否是泛型导致！",parameter_type.getName()));
                        }
                    }
                    else {
                        String[] types_name = annotation.parameters();
                        for(String type_name : types_name){
                            if(type.getTypesByName().containsKey(type_name)){
                                methodId.append("-").append(type_name);
                            }
                            else throw new RPCException(String.format("Java中的%s抽象类型参数尚未注册,请注意是否是泛型导致！",type_name));
                        }
                    }
                    methods.put(methodId.toString(),method);
                    methodId.setLength(0);
                }
            }
        }
    }
}
