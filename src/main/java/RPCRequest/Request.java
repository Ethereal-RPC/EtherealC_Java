package RPCRequest;

import Model.ClientRequestModel;
import Model.ClientResponseModel;
import Model.RPCException;
import RPCNet.NetConfig;
import RPCNet.NetCore;
import org.javatuples.Pair;
import Utils.Utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public  class Request implements InvocationHandler {
    private final ConcurrentHashMap<Integer,ClientRequestModel> tasks = new ConcurrentHashMap<>();
    private final Random random = new Random();
    private String serviceName;
    private Pair<String,String> clientKey;
    private RequestConfig config;

    public ConcurrentHashMap<Integer, ClientRequestModel> getTasks() {
        return tasks;
    }


    public static <T> T register(Class<T> interface_class, Pair<String,String> key, String serviceName, RequestConfig config){
        Request proxy = new Request();
        proxy.serviceName = serviceName;
        proxy.clientKey = key;
        proxy.config = config;
        return (T) Proxy.newProxyInstance(Request.class.getClassLoader(),new Class<?>[]{interface_class}, proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws RPCException, InvocationTargetException, IllegalAccessException {
        Annotation.RPCRequest annotation = method.getAnnotation(Annotation.RPCRequest.class);
        if(annotation != null){
            StringBuilder methodId = new StringBuilder(method.getName());
            Type factType;
            Class<?> cls;
            int param_count;
            if(args!=null)param_count = args.length;
            else param_count = 0;
            String[] array = new String[param_count + 1];
            if(annotation.parameters().length == 0){
                Class<?>[] parameters = method.getParameterTypes();
                String type_name;
                for(int i=0,j=1;i<param_count;i++,j++){
                    type_name = config.getType().getAbstractName().get(parameters[i]);
                    if(type_name != null) {
                        methodId.append("-").append(type_name);
                        array[j] = Utils.gson.toJson(args[i],parameters[i]);
                    }
                    else throw new RPCException(String.format("Java中的%s类型参数尚未注册！",parameters[i].getName()));
                }
            }
            else {
                String[] types_name = annotation.parameters();
                if(param_count == types_name.length){
                    for(int i=0,j=1;i<args.length;i++,j++){
                        factType = config.getType().getAbstractType().get(types_name[i]);
                        if(factType!=null){
                            methodId.append("-").append(types_name[i]);
                            array[j] = Utils.gson.toJson(args[i],factType);
                        }
                        else throw new RPCException(String.format("方法体%s中的抽象类型为%s的类型尚未注册！",method.getName(),types_name[i]));
                    }
                }
                else throw new RPCException(String.format("方法体%s中RPCMethod注解与实际参数数量不符,@RPCRequest:%d个,Method:%d个",method.getName(),types_name.length,args.length));
            }
            ClientRequestModel request = new ClientRequestModel("2.0", serviceName, methodId.toString(),array);
            NetConfig netConfig;
            netConfig = NetCore.Get(clientKey);
            if(netConfig == null)throw new RPCException(RPCException.ErrorCode.RuntimeError,String.format("%s-%s-%s-%s方法未找到NetConfig",clientKey.getValue0(),clientKey.getValue1(), serviceName,methodId));
            Class<?> return_type = method.getReturnType();
            if(return_type.equals(Void.TYPE)){
                netConfig.getClientRequestSend().ClientRequestSend(request);
                return null;
            }
            else{
                int id = random.nextInt();
                while (tasks.containsKey(id)){
                    id = random.nextInt();
                }
                request.setId(Integer.toString(id));
                tasks.put(id,request);
                int timeout = config.getTimeout();
                if(annotation.timeout() != -1)timeout = annotation.timeout();
                netConfig.getClientRequestSend().ClientRequestSend(request);
                ClientResponseModel respond = request.getResult(timeout);
                if(respond != null && respond.getResult() != null){
                    if(respond.getError()!=null){
                        if(respond.getError().getCode() == 0){
                            throw new RPCException(RPCException.ErrorCode.RuntimeError,"用户权限不足");
                        }
                    }
                    if(config.getType().getConvert().get(respond.getResultType())!=null){
                        return config.getType().getConvert().get(respond.getResultType()).convert(respond.getResult());
                    }
                    else throw new RPCException(RPCException.ErrorCode.RuntimeError,respond.getResultType() + "抽象数据类型尚未注册");
                }
                else return null;
            }
        }
        else return method.invoke(this,args);
    }
}
