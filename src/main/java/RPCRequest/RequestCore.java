package RPCRequest;
import java.lang.reflect.Proxy;

import Model.RPCException;
import Model.RPCTypeConfig;
import NativeClient.SocketClient;
import RPCNet.Net;
import RPCNet.NetCore;

public class RequestCore {

    public static Request get(String netName,String serviceName) throws RPCException {
        Net net = NetCore.get(netName);
        if (net == null)
            throw new RPCException(RPCException.ErrorCode.Runtime, String.format("{%s} Net未找到！", netName));
        return get(net,serviceName);
    }
    public static Request get(Net net,String serviceName) throws RPCException {
        Object request = net.getRequests().get(serviceName);
        return (Request) Proxy.getInvocationHandler(request);
    }

    public static <T> T register(Class<T> interface_class,Net net, String serviceName, RPCTypeConfig type) throws RPCException{
        return register(interface_class,net,serviceName,new RequestConfig(type));
    }

    public static <T> T register(Class<T> interface_class,Net net,String serviceName, RequestConfig config) throws RPCException {
        T service = null;
        service = (T) net.getRequests().get(serviceName);
        if(service == null){
            try{
                SocketClient socketClient = null;
                service = Request.register(interface_class,net.getName(),serviceName,config);
                net.getRequests().put(serviceName,service);
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.Core,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.Core,String.format("%s-%s已注册,无法重复注册！", net.getName(),serviceName));
        return service;
    }

    public static boolean unregister(String netName,String serviceName) throws RPCException {
        Net net = NetCore.get(netName);
        if (net == null)
            throw new RPCException(RPCException.ErrorCode.Runtime, String.format("{%s} Net未找到！", netName));
        return unregister(net, serviceName);
    }
    public static boolean unregister(Net net,String serviceName) throws RPCException {
        net.getRequests().remove(serviceName);
        return true;
    }


}
