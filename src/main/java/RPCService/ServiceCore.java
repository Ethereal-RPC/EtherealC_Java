package RPCService;

import Core.Enums.NetType;
import Core.Model.RPCException;
import Core.Model.RPCTypeConfig;
import RPCNet.Abstract.Net;
import RPCNet.NetCore;
import RPCService.Abstract.Service;
import RPCService.WebSocket.WebSocketService;

import java.lang.reflect.InvocationTargetException;

public class ServiceCore {

    public static Service get(String netName, String serviceName)  {
        Net net = NetCore.get(netName);
        if(net == null)return null;
        return get(net,serviceName);
    }
    public static Service get(Net net,String serviceName)  {
        return net.getServices().get(serviceName);
    }


    public static Service register(Object instance,Net net, String serviceName, RPCTypeConfig type) throws RPCException {
        if(net.getNetType() == NetType.WebSocket){
            return register(instance,net,serviceName,new ServiceConfig(type));
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("未有针对%s的Service-Register处理",net.getNetType()));
    }
    public static Service register(Class instanceClass,Net net,String serviceName, RPCTypeConfig type) throws RPCException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if(net.getNetType() == NetType.WebSocket){
            return register(instanceClass.getDeclaredConstructor().newInstance(),net,serviceName,new ServiceConfig(type));
        }
        else throw new RPCException(RPCException.ErrorCode.Core, String.format("未有针对%s的Service-Register处理",net.getNetType()));
    }
    public static Service register(Class instanceClass,Net net,String serviceName, ServiceConfig config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, RPCException {
        return register(instanceClass.getDeclaredConstructor().newInstance(),net,serviceName,config);
    }
    public static Service register(Object instance,Net net,String serviceName, ServiceConfig config) throws RPCException {
        Service service = net.getServices().get(serviceName);
        if(service == null){
            try{
                if(net.getNetType() == NetType.WebSocket){
                    service = new WebSocketService();
                }
                else throw new RPCException(RPCException.ErrorCode.Core, String.format("未有针对%s的Service-Register处理",net.getNetType()));
                service.register(instance,net.getName(),config);
                net.getServices().put(serviceName,service);
                service.getExceptionEvent().register(net::onException);
                service.getLogEvent().register(net::onLog);
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
