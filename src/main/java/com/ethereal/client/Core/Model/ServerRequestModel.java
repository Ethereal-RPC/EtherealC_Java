package com.ethereal.client.Core.Model;

import com.google.gson.annotations.Expose;

import java.util.Arrays;

public class ServerRequestModel {
    @Expose
    private String type = "ER-1.0-ServerRequest";
    @Expose
    private String mapping;
    @Expose
    private Object[] params;
    @Expose
    private String service;

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "ServerRequestModel{" +
                "type='" + type + '\'' +
                ", methodId='" + mapping + '\'' +
                ", params=" + Arrays.toString(params) +
                ", service='" + service + '\'' +
                '}';
    }
}
