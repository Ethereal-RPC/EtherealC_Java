package Core.Model;

import NativeClient.Abstract.Client;
import RPCNet.Abstract.Net;
import RPCRequest.Abstract.Request;
import RPCService.Abstract.Service;

public class RPCLog {
    public enum LogCode { Core, Runtime }
    private String message;
    private LogCode code;
    private Client client;
    private Service service;
    private Request request;
    private Net net;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LogCode getCode() {
        return code;
    }

    public void setCode(LogCode code) {
        this.code = code;
    }

    public RPCLog(LogCode code,String message) {
        this.message = message;
        this.code = code;
    }
}
