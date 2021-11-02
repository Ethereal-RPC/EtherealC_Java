package com.ethereal.client.Request.Interface;

import com.ethereal.client.Core.Interface.IExceptionEvent;
import com.ethereal.client.Core.Interface.ILogEvent;

public interface IRequest extends IExceptionEvent, ILogEvent {
    void initialize();
    void unInitialize();
}
