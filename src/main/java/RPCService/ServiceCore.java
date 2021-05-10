package RPCService;

import Model.RPCException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import Model.RPCTypeConfig;
import RPCNet.Net;
import RPCNet.NetCore;
import org.javatuples.Pair;
import org.javatuples.Triplet;

public class ServiceCore {
    public static void register(Object instance, String ip, String port, String serviceName, RPCTypeConfig type) throws RPCException {
        register(instance, ip,port,serviceName,new ServiceConfig(type));
    }
    public static void register(Class instanceClass,  String ip, String port,String serviceName, RPCTypeConfig type) throws RPCException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        register(instanceClass.getDeclaredConstructor().newInstance(), ip,port,serviceName,new ServiceConfig(type));
    }
    public static void register(Class instanceClass,  String ip, String port,String serviceName, ServiceConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, RPCException {
        register(instanceClass.getDeclaredConstructor().newInstance(), ip,port,serviceName,config);
    }
    public static void register(Object instance,String ip, String port,  String serviceName, ServiceConfig config) throws RPCException {
        Net net = NetCore.Get(new Pair<>(ip,port));
        if(net == null)throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("{%s}-{%s} Net未找到！", ip,port));
        Service service = net.getServices().get(serviceName);
        if(service == null){
            try{
                service = new Service();
                service.register(instance,config.getTypes());
                net.getServices().put(serviceName,service);
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.RegisterError,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.RegisterError,String.format("%s-%s-%s已注册,无法重复注册！", ip,port,serviceName));
    }

    public static void unregister( String ip, String port,String serviceName) throws RPCException {
        Net net = NetCore.Get(new Pair<>(ip,port));
        if(net == null)throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("{%s}-{%s} Net未找到！", ip,port));
        if(net.getServices().containsKey(serviceName)){
            net.getServices().remove(serviceName);
        }
    }
    public static Service get(String ip, String port,String serviceName) throws RPCException {
        Net net = NetCore.Get(new Pair<>(ip,port));
        if(net == null)throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("{%s}-{%s} Net未找到！", ip,port));
        return net.getServices().get(serviceName);
    }
    public static Service get(Triplet<String,String,String> key) throws RPCException {
        return get(key.getValue0(),key.getValue1(),key.getValue2());
    }
}
