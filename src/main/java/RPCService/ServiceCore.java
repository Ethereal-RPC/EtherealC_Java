package RPCService;

import Model.RPCException;

import java.lang.reflect.InvocationTargetException;

import Model.RPCLog;
import Model.RPCTypeConfig;
import RPCNet.Net;
import RPCNet.NetCore;
import RPCService.Event.ExceptionEvent;
import RPCService.Event.LogEvent;

public class ServiceCore {

    public static Service get(String netName,String serviceName)  {
        Net net = NetCore.get(netName);
        if(net == null)return null;
        return get(net,serviceName);
    }
    public static Service get(Net net,String serviceName)  {
        return net.getServices().get(serviceName);
    }


    public static Service register(Object instance,Net net, String serviceName, RPCTypeConfig type) throws RPCException {
        return register(instance,net,serviceName,new ServiceConfig(type));
    }
    public static Service register(Class instanceClass,Net net,String serviceName, RPCTypeConfig type) throws RPCException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return register(instanceClass.getDeclaredConstructor().newInstance(),net,serviceName,new ServiceConfig(type));
    }
    public static Service register(Class instanceClass,Net net,String serviceName, ServiceConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, RPCException {
        return register(instanceClass.getDeclaredConstructor().newInstance(),net,serviceName,config);
    }
    public static Service register(Object instance,Net net,String serviceName, ServiceConfig config) throws RPCException {
        Service service = net.getServices().get(serviceName);
        if(service == null){
            try{
                service = new Service();
                service.register(instance,net.getName(),config);
                net.getServices().put(serviceName,service);
                service.getExceptionEvent().register(net::OnServiceException);
                service.getLogEvent().register(net::OnServiceLog);
                return service;
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.Core,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.Core,String.format("%s已注册,无法重复注册！",net.getName(),serviceName));
    }

    public static boolean unregister(String netName,String serviceName) throws RPCException {
        Net net = NetCore.get(netName);
        return unregister(net,serviceName);
    }
    public static boolean unregister(Net net,String serviceName) {
        if(net != null){
            if(net.getServices().containsKey(serviceName)){
                net.getServices().remove(serviceName);
            }
        }
        return true;
    }
}
