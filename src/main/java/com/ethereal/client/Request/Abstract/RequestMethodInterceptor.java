package com.ethereal.client.Request.Abstract;

import com.ethereal.client.Core.Model.AbstractType;
import com.ethereal.client.Core.Model.ClientRequestModel;
import com.ethereal.client.Core.Model.ClientResponseModel;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Request.Annotation.InvokeTypeFlags;
import com.ethereal.client.Request.Annotation.RequestMethod;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
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
        RequestMethod annotation = method.getAnnotation(RequestMethod.class);
        Object localResult = null;
        Object remoteResult = null;
        if((annotation.invokeType() & InvokeTypeFlags.Local) == 0){
            StringBuilder methodId = new StringBuilder(method.getName());
            Parameter[] parameterInfos = method.getParameters();
            ArrayList<String> params = new ArrayList<>(parameterInfos.length - 1);
            for(int i = 0; i< parameterInfos.length; i++){
                AbstractType type = instance.getTypes().getTypesByType().get(parameterInfos[i].getParameterizedType());
                if(type == null)type = instance.getTypes().getTypesByName().get(method.getAnnotation(com.ethereal.client.Core.Annotation.AbstractType.class).abstractName());
                if(type == null)throw new TrackException(TrackException.ErrorCode.Runtime,String.format("RPC中的%s类型参数尚未被注册！",parameterInfos[i].getParameterizedType()));
                methodId.append("-").append(type.getName());
                params.add(type.getSerialize().Serialize(args[i]));
            }
            ClientRequestModel request = new ClientRequestModel("2.0", instance.name, methodId.toString(),params.toArray(new String[]{}));
            Class<?> return_type = method.getReturnType();
            if(return_type.equals(Void.TYPE)){
                instance.getNet().getClient().sendClientRequestModel(request);
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
                    if(instance.getNet().getClient().sendClientRequestModel(request)){
                        ClientResponseModel respond = request.getResult(timeout);
                        if(respond != null){
                            if(respond.getError()!=null){
                                if((annotation.invokeType() & InvokeTypeFlags.Fail) != 0){
                                    localResult = methodProxy.invokeSuper(instance,args);
                                }
                                else throw new TrackException(TrackException.ErrorCode.Runtime,"来自服务端的报错信息：\n" + respond.getError().getMessage());
                            }
                            AbstractType type = instance.getTypes().getTypesByType().get(return_type);
                            if(type == null)type = instance.getTypes().getTypesByName().get(method.getAnnotation(com.ethereal.client.Core.Annotation.AbstractType.class).abstractName());
                            if(type == null)throw new TrackException(TrackException.ErrorCode.Runtime,String.format("RPC中的%s类型参数尚未被注册！",return_type));
                            remoteResult = type.getDeserialize().Deserialize(respond.getResult());
                            if((annotation.invokeType() & InvokeTypeFlags.Success) != 0
                                    || (annotation.invokeType() & InvokeTypeFlags.All) != 0){
                                localResult = methodProxy.invokeSuper(instance,args);
                            }
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
