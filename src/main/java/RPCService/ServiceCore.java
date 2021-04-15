package RPCService;

import Model.RPCException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import Model.RPCType;
import Model.ServerRequestModel;
import RPCNet.NetConfig;
import org.javatuples.Triplet;

public class ServiceCore {
    //Java没有自带三元组，这里就引用Kotlin了.
    public static HashMap<Triplet<String,String,String>, Service> services = new HashMap<>();
    public static void register(Object instance, String ip, String port,  String serviceName,RPCType type) throws RPCException {
        register(instance, ip,port,serviceName,new ServiceConfig(type));
    }
    public static void register(Class instanceClass,  String ip, String port,String serviceName, RPCType type) throws RPCException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        register(instanceClass.getDeclaredConstructor().newInstance(), ip,port,serviceName,new ServiceConfig(type));
    }
    public static void register(Class instanceClass,  String ip, String port,String serviceName, ServiceConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, RPCException {
        register(instanceClass.getDeclaredConstructor().newInstance(), ip,port,serviceName,config);
    }
    public static void register(Object instance,String ip, String port,  String serviceName, ServiceConfig config) throws RPCException {
        Triplet<String,String,String> key = new Triplet<>( ip,port,serviceName);
        Service service = services.get(key);
        if(service == null){
            try{
                service = new Service();
                service.register(instance,config.getType());
                services.put(key,service);
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.RegisterError,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.RegisterError,String.format("%s-%s-%s已注册,无法重复注册！", ip,port,serviceName));
    }

    public static void unregister( String hostname, String port,String serviceName){
        Triplet<String,String,String> key = new Triplet<>(hostname,port,serviceName);
        if(services.containsKey(key)){
            services.remove(key);
        }
    }
    public static Service get(String hostname, String port,String serviceName){
        Triplet<String,String,String> key = new Triplet<>(hostname,port,serviceName);
        return services.get(key);
    }
    public static Service get(Triplet<String,String,String> key){
        return services.get(key);
    }


}
