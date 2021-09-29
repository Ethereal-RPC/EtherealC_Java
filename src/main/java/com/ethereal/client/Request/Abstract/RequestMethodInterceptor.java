package com.ethereal.client.Request.Abstract;

import com.ethereal.client.Core.Model.AbstractType;
import com.ethereal.client.Core.Model.ClientRequestModel;
import com.ethereal.client.Core.Model.ClientResponseModel;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Request.Annotation.InvokeTypeFlags;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Random;

public class RequestMethodInterceptor implements MethodInterceptor {
    private Request instance;
    private Random random = new Random();

    public Request getInstance() {
        return instance;
    }

    public void setInstance(Request instance) {
        this.instance = instance;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        com.ethereal.client.Request.Annotation.Request annotation = method.getAnnotation(com.ethereal.client.Request.Annotation.Request.class);
        Object localResult = null;
        Object remoteResult = null;
        if((annotation.invokeType() & InvokeTypeFlags.Local) == 0){
            StringBuilder methodId = new StringBuilder(method.getName());
            int param_count;
            if(args!=null)param_count = args.length;
            else param_count = 0;
            String[] array = new String[param_count + 1];
            if(annotation.parameters().length == 0){
                Class<?>[] parameters = method.getParameterTypes();
                for(int i=0,j=1;i<param_count;i++,j++){
                    AbstractType rpcType = instance.config.getType().getTypesByType().get(parameters[i]);
                    if(rpcType != null) {
                        methodId.append("-").append(rpcType.getName());
                        array[j] = rpcType.getSerialize().Serialize(args[i]);
                    }
                    else throw new TrackException(TrackException.ErrorCode.Runtime,String.format("Java中的%s类型参数尚未注册！",parameters[i].getName()));
                }
            }
            else {
                String[] types_name = annotation.parameters();
                if(param_count == types_name.length){
                    for(int i=0,j=1;i<args.length;i++,j++){
                        AbstractType rpcType = instance.config.getType().getTypesByName().get(types_name[i]);
                        if(rpcType!=null){
                            methodId.append("-").append(rpcType.getName());
                            array[j] = rpcType.getSerialize().Serialize(args[i]);
                        }
                        else throw new TrackException(TrackException.ErrorCode.Runtime,String.format("方法体%s中的抽象类型为%s的类型尚未注册！",method.getName(),types_name[i]));
                    }
                }
                else throw new TrackException(TrackException.ErrorCode.Runtime,String.format("方法体%s中RPCMethod注解与实际参数数量不符,@RPCRequest:%d个,Method:%d个",method.getName(),types_name.length,args.length));
            }
            ClientRequestModel request = new ClientRequestModel("2.0", instance.name, methodId.toString(),array);
            Class<?> return_type = method.getReturnType();
            if(return_type.equals(Void.TYPE)){
                instance.client.sendClientRequestModel(request);
            }
            else{
                int id = random.nextInt();
                while (instance.getTasks().containsKey(id)){
                    id = random.nextInt();
                }
                request.setId(Integer.toString(id));
                instance.tasks.put(id,request);
                try {
                    int timeout = instance.config.getTimeout();
                    if(annotation.timeout() != -1)timeout = annotation.timeout();
                    if(instance.client.sendClientRequestModel(request)){
                        ClientResponseModel respond = request.getResult(timeout);
                        if(respond != null){
                            if(respond.getError()!=null){
                                if((annotation.invokeType() & InvokeTypeFlags.Fail) != 0){
                                    localResult = methodProxy.invokeSuper(instance,args);
                                }
                                else throw new TrackException(TrackException.ErrorCode.Runtime,"来自服务端的报错信息：\n" + respond.getError().getMessage());
                            }
                            AbstractType rpcType = instance.config.getType().getTypesByName().get(respond.getResultType());
                            if(rpcType!=null){
                                remoteResult = rpcType.getDeserialize().Deserialize(respond.getResult());
                                if((annotation.invokeType() & InvokeTypeFlags.Success) != 0
                                        || (annotation.invokeType() & InvokeTypeFlags.All) != 0){
                                    localResult = methodProxy.invokeSuper(instance,args);
                                }
                            }
                            else throw new TrackException(TrackException.ErrorCode.Runtime,respond.getResultType() + "抽象数据类型尚未注册");
                        }
                        else if((annotation.invokeType() & InvokeTypeFlags.Timeout) != 0){
                            localResult = methodProxy.invokeSuper(instance,args);
                        }
                    }
                }
                finally {
                    instance.tasks.remove(id);
                }
            }
        }
        else localResult = methodProxy.invokeSuper(instance,args);
        if((annotation.invokeType() & InvokeTypeFlags.ReturnRemote) != 0){
            return remoteResult;
        }
        else if((annotation.invokeType() & InvokeTypeFlags.ReturnLocal) != 0){
            return localResult;
        }
        else {
            return remoteResult;
        }
    }
}
