package Request.WebSocket;

import Core.Model.ClientRequestModel;
import Core.Model.ClientResponseModel;
import Core.Model.RPCException;
import Core.Model.RPCType;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

public class WebSocketRequest extends Request.Abstract.Request {
    private final Random random = new Random();

    public static <T> T register(Class<T> interface_class, String netName, String serviceName, Request.Abstract.RequestConfig config){
        Request.Abstract.Request proxy = new WebSocketRequest();
        proxy.setName(serviceName);
        proxy.setNetName(netName);
        proxy.setConfig(config);
        return (T)Proxy.newProxyInstance(Request.Abstract.Request.class.getClassLoader(),new Class<?>[]{interface_class}, proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        Request.Annotation.Request annotation = method.getAnnotation(Request.Annotation.Request.class);
        if(annotation != null){
            StringBuilder methodId = new StringBuilder(method.getName());
            int param_count;
            if(args!=null)param_count = args.length;
            else param_count = 0;
            String[] array = new String[param_count + 1];
            if(annotation.parameters().length == 0){
                Class<?>[] parameters = method.getParameterTypes();
                for(int i=0,j=1;i<param_count;i++,j++){
                    RPCType rpcType = config.getType().getTypesByType().get(parameters[i]);
                    if(rpcType != null) {
                        methodId.append("-").append(rpcType.getName());
                        array[j] = rpcType.getSerialize().Serialize(args[i]);
                    }
                    else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("Java中的%s类型参数尚未注册！",parameters[i].getName()));
                }
            }
            else {
                String[] types_name = annotation.parameters();
                if(param_count == types_name.length){
                    for(int i=0,j=1;i<args.length;i++,j++){
                        RPCType rpcType = config.getType().getTypesByName().get(types_name[i]);
                        if(rpcType!=null){
                            methodId.append("-").append(rpcType.getName());
                            array[j] = rpcType.getSerialize().Serialize(args[i]);
                        }
                        else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("方法体%s中的抽象类型为%s的类型尚未注册！",method.getName(),types_name[i]));
                    }
                }
                else throw new RPCException(RPCException.ErrorCode.Runtime,String.format("方法体%s中RPCMethod注解与实际参数数量不符,@RPCRequest:%d个,Method:%d个",method.getName(),types_name.length,args.length));
            }
            ClientRequestModel request = new ClientRequestModel("2.0", name, methodId.toString(),array);
            Class<?> return_type = method.getReturnType();
            if(return_type.equals(Void.TYPE)){
                client.sendClientRequestModel(request);
            }
            else{
                int id = random.nextInt();
                while (tasks.containsKey(id)){
                    id = random.nextInt();
                }
                request.setId(Integer.toString(id));
                tasks.put(id,request);
                try {
                    int timeout = config.getTimeout();
                    if(annotation.timeout() != -1)timeout = annotation.timeout();
                    if(client.sendClientRequestModel(request)){
                        ClientResponseModel respond = request.getResult(timeout);
                        if(respond != null){
                            if(respond.getError()!=null){
                                throw new RPCException(RPCException.ErrorCode.Runtime,"来自服务端的报错信息：\n" + respond.getError().getMessage());
                            }
                            RPCType rpcType = config.getType().getTypesByName().get(respond.getResultType());
                            if(rpcType!=null){
                                return rpcType.getDeserialize().Deserialize(respond.getResult());
                            }
                            else throw new RPCException(RPCException.ErrorCode.Runtime,respond.getResultType() + "抽象数据类型尚未注册");
                        }
                    }
                }
                finally {
                    tasks.remove(id);
                }
            }
            return null;
        }
        else return method.invoke(this,args);
    }
}
