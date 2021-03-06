package com.ethereal.client.Core.Manager.Ioc;

import com.ethereal.client.Core.Manager.Event.EventManager;
import com.ethereal.client.Core.Manager.Event.Model.EventContext;
import com.ethereal.client.Core.Model.TrackException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class IocManager {
    private final EventManager eventManager = new EventManager();
    private final HashMap<String,Object> iocContainer = new HashMap<>();

    public void register(String name, Object instance) throws TrackException {
        if(iocContainer.containsKey(name)){
            throw new TrackException(TrackException.ErrorCode.Runtime, String.format("已经注册%sIOC实例",name));
        }
        iocContainer.put(name,instance);
        eventManager.register(name,instance);
    }

    public void unregister(String name) {
        if(iocContainer.containsKey(name)){
            Object instance = iocContainer.get(name);
            iocContainer.remove(name);
            eventManager.unregister(name,instance);
        }
    }

    public Object get(String name) {
        return iocContainer.get(name);
    }
    public void invokeEvent(Object instance,String function, HashMap<String, Object> params, EventContext context) throws TrackException, InvocationTargetException, IllegalAccessException {
        eventManager.invokeEvent(instance,function,params,context);
    }
}
