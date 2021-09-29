package com.ethereal.client.Net.Interface;

import com.ethereal.client.Core.Interface.IExceptionEvent;
import com.ethereal.client.Core.Interface.ILogEvent;
import com.ethereal.client.Core.Model.ClientResponseModel;
import com.ethereal.client.Core.Model.ServerRequestModel;

public interface INet extends IExceptionEvent, ILogEvent {
    boolean publish() throws Exception;
    void serverRequestReceiveProcess(ServerRequestModel request) throws Exception;
    void clientResponseProcess(ClientResponseModel response) throws Exception;
}
