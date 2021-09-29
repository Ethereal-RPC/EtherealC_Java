package com.ethereal.client.Core.Model;

import com.ethereal.client.Client.Abstract.Client;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Request.Abstract.Request;
import com.ethereal.client.Service.Abstract.Service;

public class TrackLog {
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

    public TrackLog(LogCode code, String message) {
        this.message = message;
        this.code = code;
    }
}
