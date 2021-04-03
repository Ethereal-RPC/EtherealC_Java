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
    public static void register(Object instance, String serviceName, String ip, String port, RPCType type) throws RPCException {
        register(instance,serviceName, ip,port,new ServiceConfig(type));
    }
    public static void register(Class instanceClass, String serviceName, String ip, String port, RPCType type) throws RPCException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        register(instanceClass.getDeclaredConstructor().newInstance(),serviceName, ip,port,new ServiceConfig(type));
    }
    public static void register(Class instanceClass, String serviceName, String ip, String port, ServiceConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, RPCException {
        register(instanceClass.getDeclaredConstructor().newInstance(),serviceName, ip,port,config);
    }
    public static void register(Object instance, String serviceName, String ip, String port, ServiceConfig config) throws RPCException {
        Triplet<String,String,String> key = new Triplet<>(serviceName, ip,port);
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

    public static void unregister(String serviceName, String hostname, String port){
        Triplet<String,String,String> key = new Triplet<>(serviceName,hostname,port);
        if(services.containsKey(key)){
            services.remove(key);
        }
    }
    public static Service get(String serviceName, String hostname, String port){
        Triplet<String,String,String> key = new Triplet<>(serviceName,hostname,port);
        return services.get(key);
    }
    public static Service get(Triplet<String,String,String> key){
        return services.get(key);
    }

    public static void ServerRequestReceive(String ip, String port, NetConfig config, ServerRequestModel request) throws InvocationTargetException, IllegalAccessException, RPCException {
        Method method;
        Service service = services.get(new Triplet<>(request.getService(),ip,port));
        if(service != null){
            method = service.getMethods().get(request.getMethodId());
            if(method!= null){
                service.ConvertParams(request.getMethodId(),request.getParams());
                method.invoke(service.getInstance(),request.getParams());
            }
            else {
                throw new RPCException(RPCException.ErrorCode.NotFoundService,String.format("%s-%s-%s-%s Not Found",ip,port,request.getService(),request.getMethodId()));
            }
        }
        else {
            throw new RPCException(RPCException.ErrorCode.NotFoundService,String.format("%s-%s-%s Not Found",ip,port,request.getService()));
        }
    }
}
