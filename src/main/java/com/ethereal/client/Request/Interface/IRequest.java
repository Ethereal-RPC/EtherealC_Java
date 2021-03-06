package com.ethereal.client.Request.Interface;

import com.ethereal.client.Core.Interface.IExceptionEvent;
import com.ethereal.client.Core.Interface.ILogEvent;
import com.ethereal.client.Core.Model.TrackException;

public interface IRequest{
    void initialize() throws TrackException;
    void register();
    void unregister();
    void unInitialize();
}
