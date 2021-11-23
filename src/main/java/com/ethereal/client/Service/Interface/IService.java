package com.ethereal.client.Service.Interface;

import com.ethereal.client.Core.Interface.IExceptionEvent;
import com.ethereal.client.Core.Interface.ILogEvent;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Service.Abstract.ServiceConfig;

public interface IService {
    void initialize() throws TrackException;
    void register();
    void unregister();
    void unInitialize();
}
