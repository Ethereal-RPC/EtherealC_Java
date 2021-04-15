package RPCRequest;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import Model.ClientRequestModel;
import Model.ClientResponseModel;
import Model.RPCException;
import Model.RPCType;
import NativeClient.SocketClient;
import RPCNet.NetConfig;
import org.javatuples.Pair;
import org.javatuples.Triplet;

public class RequestCore {
    static HashMap<Triplet<String,String,String>,Object> requests = new HashMap<>();

    public static <T> T register(Class<T> interface_class, String hostname, String port, String serviceName, RPCType type) throws RPCException{
        return register(interface_class,hostname,port,serviceName,new RequestConfig(type));
    }

    public static <T> T register(Class<T> interface_class,  String ip, String port,String serviceName, RequestConfig config) throws RPCException {
        T service = null;
        Triplet<String,String,String> key = new Triplet<String,String,String>(ip,port,serviceName);
        service = (T) requests.get(key);
        if(service == null){
            try{
                SocketClient socketClient = null;
                Pair<String,String> clientKey = new Pair<String,String>(ip,port);
                service = Request.register(interface_class,clientKey,serviceName,config);
                requests.put(key,service);
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.RegisterError,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.RegisterError,String.format("%s-%s-%s已注册,无法重复注册！", ip,port,serviceName));
        return service;
    }

    public static void unregister( String hostname, String port,String serviceName){
        Triplet<String,String,String> key = new Triplet<>(hostname,port,serviceName);
        requests.remove(key);
    }
    public static Request get(Triplet<String,String,String> key){
        Object request = requests.get(key);
        return (Request) Proxy.getInvocationHandler(request);
    }


}
