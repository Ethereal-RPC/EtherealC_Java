package com.ethereal.client.Core.Interface;

import com.ethereal.client.Core.Model.TrackException;

public interface IBaseIoc {
    public void registerIoc(String name, Object instance) throws TrackException;
    public void unregisterIoc(String name);
    public Object getIocObject(String name);
}
