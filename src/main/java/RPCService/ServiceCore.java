package RPCService;

import Model.RPCException;

import java.lang.reflect.InvocationTargetException;

import Model.RPCTypeConfig;
import RPCNet.Net;
import RPCNet.NetCore;

public class ServiceCore {

    public static Service get(String netName,String serviceName) throws RPCException {
        Net net = NetCore.get(netName);
        if(net == null)throw new RPCException(RPCException.ErrorCode.Runtime,String.format("{%s} Net未找到！",netName));
        return get(net,serviceName);
    }
    public static Service get(Net net,String serviceName) throws RPCException {
        return net.getServices().get(serviceName);
    }


    public static void register(Object instance,Net net, String serviceName, RPCTypeConfig type) throws RPCException {
        register(instance,net,serviceName,new ServiceConfig(type));
    }
    public static void register(Class instanceClass,Net net,String serviceName, RPCTypeConfig type) throws RPCException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        register(instanceClass.getDeclaredConstructor().newInstance(),net,serviceName,new ServiceConfig(type));
    }
    public static void register(Class instanceClass,Net net,String serviceName, ServiceConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, RPCException {
        register(instanceClass.getDeclaredConstructor().newInstance(),net,serviceName,config);
    }
    public static void register(Object instance,Net net,String serviceName, ServiceConfig config) throws RPCException {
        Service service = net.getServices().get(serviceName);
        if(service == null){
            try{
                service = new Service();
                service.register(instance,net.getName(),config);
                net.getServices().put(serviceName,service);
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.Core,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.Core,String.format("%s已注册,无法重复注册！",net.getName(),serviceName));
    }

    public static boolean unregister(String netName,String serviceName) throws RPCException {
        Net net = NetCore.get(netName);
        if(net == null)throw new RPCException(RPCException.ErrorCode.Runtime,String.format("{%s} Net未找到！",netName));
        else return unregister(net,serviceName);
    }
    public static boolean unregister(Net net,String serviceName) {
        if(net.getServices().containsKey(serviceName)){
            net.getServices().remove(serviceName);
            return true;
        }
        return false;
    }

}
