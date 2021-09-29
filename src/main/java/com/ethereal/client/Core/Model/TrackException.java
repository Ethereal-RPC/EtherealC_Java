package com.ethereal.client.Core.Model;

import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Service.Abstract.Service;

public class TrackException extends java.lang.Exception {
    public enum ErrorCode {Core, Runtime, NotEthereal}
    private java.lang.Exception exception;
    private ErrorCode errorCode;
    private Client client;
    private Service service;
    private Request request;
    private Net net;


    public java.lang.Exception getException() {
        return exception;
    }

    public void setException(java.lang.Exception exception) {
        this.exception = exception;
    }
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Net getNet() {
        return net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public TrackException(ErrorCode errorCode, String message)
    {
        super(message);
        this.exception = this;
        this.errorCode = errorCode;
    }
    public TrackException(java.lang.Exception e)
    {
        super("外部库错误");
        this.exception = e;
        this.errorCode = ErrorCode.NotEthereal;
    }
}
