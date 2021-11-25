package com.ethereal.client.Request.Abstract;

import com.ethereal.client.Core.Manager.AbstractType.AbstractType;
import com.ethereal.client.Core.Manager.AbstractType.Param;
import com.ethereal.client.Core.Manager.Event.Annotation.AfterEvent;
import com.ethereal.client.Core.Manager.Event.Annotation.BeforeEvent;
import com.ethereal.client.Core.Manager.Event.Model.AfterEventContext;
import com.ethereal.client.Core.Manager.Event.Model.BeforeEventContext;
import com.ethereal.client.Core.Manager.Event.Model.EventContext;
import com.ethereal.client.Core.Manager.Event.Model.ExceptionEventContext;
import com.ethereal.client.Core.Model.ClientRequestModel;
import com.ethereal.client.Core.Model.ClientResponseModel;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Request.Annotation.InvokeTypeFlags;
import com.ethereal.client.Request.Annotation.RequestMapping;
import com.ethereal.client.Request.Event.Annotation.FailEvent;
import com.ethereal.client.Request.Event.Annotation.SuccessEvent;
import com.ethereal.client.Request.Event.Annotation.TimeoutEvent;
import com.ethereal.client.Request.Event.Model.FailEventContext;
import com.ethereal.client.Request.Event.Model.SuccessEventContext;
import com.ethereal.client.Request.Event.Model.TimeoutEventContext;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Random;

public class RequestInterceptor implements MethodInterceptor {
    private final Random random = new Random();

    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Request instance = (Request) o;
        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
        Object localResult = null;
        Object remoteResult = null;
        Object methodResult = null;
        EventContext eventContext;
        Parameter[] parameterInfos = method.getParameters();
        ClientRequestModel request = new ClientRequestModel();
        request.setMapping(annotation.mapping());
        request.setParams(new HashMap<>(parameterInfos.length -1 ));
        HashMap<String,Object> params = new HashMap<>(parameterInfos.length);
        int idx = 0;
        for(Parameter parameterInfo : parameterInfos){
            AbstractType type = instance.getTypes().get(parameterInfo);
            request.getParams().put(parameterInfo.getName(),type.getSerialize().Serialize(args[idx]));
            params.put(parameterInfo.getName(), args[idx++]);
        }
        BeforeEvent beforeEvent = method.getAnnotation(BeforeEvent.class);
        if(beforeEvent != null){
            eventContext = new BeforeEventContext(params,method);
            String iocObjectName = beforeEvent.function().substring(0, beforeEvent.function().indexOf("."));
            instance.getIocManager().invokeEvent(instance.getIocManager().get(iocObjectName), beforeEvent.function(), params,eventContext);
        }
        if((annotation.invokeType() & InvokeTypeFlags.Local) == 0) {
            try{
                localResult = methodProxy.invokeSuper(instance,args);
            }
            catch (Exception e){
                com.ethereal.client.Core.Manager.Event.Annotation.ExceptionEvent exceptionEvent = method.getAnnotation(com.ethereal.client.Core.Manager.Event.Annotation.ExceptionEvent.class);
                if(exceptionEvent != null){
                    eventContext = new ExceptionEventContext(params,method,e);
                    String iocObjectName = exceptionEvent.function().substring(0, exceptionEvent.function().indexOf("."));
                    instance.getIocManager().invokeEvent(instance.getIocManager().get(iocObjectName), exceptionEvent.function(),params,eventContext);
                    if(exceptionEvent.isThrow())throw e;
                }
                else throw e;
            }
        }
        AfterEvent afterEvent = method.getAnnotation(AfterEvent.class);
        if(afterEvent != null){
            eventContext = new AfterEventContext(params,method,localResult);
            String iocObjectName = afterEvent.function().substring(0,afterEvent.function().indexOf("."));
            instance.getIocManager().invokeEvent(instance.getIocManager().get(iocObjectName), afterEvent.function(), params,eventContext);
        }
        if((annotation.invokeType() & InvokeTypeFlags.Remote) != 0){
            Class<?> return_type = method.getReturnType();
            if(return_type.equals(Void.TYPE)){
                instance.getClient().sendClientRequestModel(request);
            }
            else{
                int id = random.nextInt();
                while (instance.getTasks().containsKey(id)){
                    id = random.nextInt();
                }
                request.setId(Integer.toString(id));
                instance.getTasks().put(id,request);
                try {
                    int timeout = instance.config.getTimeout();
                    if(annotation.timeout() != -1)timeout = annotation.timeout();
                    if(instance.getClient().sendClientRequestModel(request)){
                        ClientResponseModel respond = request.getResult(timeout);
                        if(respond != null){
                            if(respond.getError()!=null){
                                FailEvent failEvent = method.getAnnotation(FailEvent.class);
                                if(failEvent != null){
                                    eventContext = new FailEventContext(params,method,respond.getError());
                                    String iocObjectName = failEvent.function().substring(0, failEvent.function().indexOf("."));
                                    instance.getIocManager().invokeEvent(instance.getIocManager().get(iocObjectName), failEvent.function(), params,eventContext);
                                }
                                else throw new TrackException(TrackException.ErrorCode.Runtime,"来自服务端的报错信息：\n" + respond.getError().getMessage());
                            }
                            Param paramAnnotation = method.getAnnotation(Param.class);
                            AbstractType type = null;
                            if(paramAnnotation != null) type = instance.getTypes().getTypesByName().get(paramAnnotation.type());
                            if(type == null)type = instance.getTypes().getTypesByType().get(return_type);
                            if(type == null)throw new TrackException(TrackException.ErrorCode.Runtime,String.format("RPC中的%s类型参数尚未被注册！",return_type));
                            remoteResult = type.getDeserialize().Deserialize(respond.getResult());
                            SuccessEvent successEvent = method.getAnnotation(SuccessEvent.class);
                            if(successEvent != null){
                                eventContext = new SuccessEventContext(params,method,respond.getResult());
                                String iocObjectName = successEvent.function().substring(0, successEvent.function().indexOf("."));
                                instance.getIocManager().invokeEvent(instance.getIocManager().get(iocObjectName), successEvent.function(),params,eventContext);
                            }
                        }
                        TimeoutEvent timeoutEvent =  method.getAnnotation(TimeoutEvent.class);
                        if(timeoutEvent != null){
                            eventContext = new TimeoutEventContext(params,method);
                            String iocObjectName = beforeEvent.function().substring(0, timeoutEvent.function().indexOf("."));
                            instance.getIocManager().invokeEvent(instance.getIocManager().get(iocObjectName), timeoutEvent.function(),params,eventContext);
                        }
                    }
                }
                finally {
                    instance.getTasks().remove(id);
                }
            }
        }
        if((annotation.invokeType() & InvokeTypeFlags.ReturnRemote) != 0){
            methodResult = remoteResult;
        }
        else if((annotation.invokeType() & InvokeTypeFlags.ReturnLocal) != 0){
            methodResult = localResult;
        }
        return methodResult;
    }
}
