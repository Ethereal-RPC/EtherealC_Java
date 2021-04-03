package RPCRequest;
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

    public static <T> T register(Class<T> interface_class, String serviceName, String hostname, String port, RPCType type) throws RPCException{
        return register(interface_class,serviceName,hostname,port,new RequestConfig(type));
    }

    public static <T> T register(Class<T> interface_class, String serviceName, String ip, String port, RequestConfig config) throws RPCException {
        T service = null;
        Triplet<String,String,String> key = new Triplet<String,String,String>(serviceName, ip,port);
        service = (T) requests.get(key);
        if(service == null){
            try{
                SocketClient socketClient = null;
                Pair<String,String> clientKey = new Pair<String,String>(ip,port);
                service = Request.register(interface_class,serviceName,clientKey,config);
                requests.put(key,service);
            }
            catch (Exception err){
                throw new RPCException(RPCException.ErrorCode.RegisterError,serviceName + "异常报错，销毁注册\n" + err.getMessage());
            }
        }
        else throw new RPCException(RPCException.ErrorCode.RegisterError,String.format("%s-%s-%s已注册,无法重复注册！", ip,port,serviceName));
        return service;
    }

    public static void unregister(String serviceName, String hostname, String port){
        Triplet<String,String,String> key = new Triplet<>(serviceName,hostname,port);
        if(requests.containsKey(key)){
            requests.remove(key);
        }
    }
    public static Request get(Triplet<String,String,String> key){
        return (Request) requests.get(key);
    }

    public static void ClientResponseProcess(String ip, String port, NetConfig netConfig, ClientResponseModel response) throws RPCException {
        int id = Integer.parseInt(response.getId());
        Request request = RequestCore.get(new Triplet<>(ip,port,response.getService()));
        if(request != null){
            ClientRequestModel requestModel = request.getTasks().get(Integer.parseInt((response.getId())));
            requestModel.setResult(response);
        }
        else if(netConfig.isDebug())throw new RPCException(RPCException.ErrorCode.NotFoundRequest,String.format("%s-%s-%s Request未找到",ip,port,response.getService()));
    }
}
