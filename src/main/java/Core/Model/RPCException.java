package Core.Model;

import NativeClient.Abstract.Client;
import RPCNet.Abstract.Net;
import RPCRequest.Abstract.Request;
import RPCService.Abstract.Service;

public class RPCException extends Exception{
    public enum ErrorCode {Core, Runtime, NotEthereal}
    private Exception exception;
    private ErrorCode errorCode;
    private Client client;
    private Service service;
    private Request request;
    private Net net;


    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
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

    public RPCException(ErrorCode errorCode,String message)
    {
        super(message);
        this.exception = this;
        this.errorCode = errorCode;
    }
    public RPCException(Exception e)
    {
        super("外部库错误");
        this.exception = e;
        this.errorCode = ErrorCode.NotEthereal;
    }
}
