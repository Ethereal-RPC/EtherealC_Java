package com.ethereal.client.Core.Model;

import com.google.gson.annotations.Expose;

import java.util.Arrays;

public class ClientRequestModel {
    @Expose(serialize = false,deserialize = false)
    private ClientResponseModel Result;
    @Expose
    private String Type = "ER-1.0-ClientRequest";
    @Expose
    private String MethodId;
    @Expose
    private String[] Params;
    @Expose
    private String Id;
    @Expose
    private String Service;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMethodId() {
        return MethodId;
    }

    public void setMethodId(String methodId) {
        MethodId = methodId;
    }

    public String[] getParams() {
        return Params;
    }

    public void setParams(String[] params) {
        Params = params;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getService() {
        return Service;
    }

    public void setService(String service) {
        Service = service;
    }

    public ClientRequestModel(String jsonRpc, String service, String methodId, String[] params) {
        Type = jsonRpc;
        MethodId = methodId;
        Params = params;
        Service = service;
    }

    public void setResult(ClientResponseModel result) {
        synchronized (this){
            Result = result;
            this.notify();
        }
    }

    public ClientResponseModel getResult(int timeout)  {
        synchronized (this){
            if (Result == null){
                try {
                    if(timeout == -1)this.wait();
                    else this.wait(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return Result;
        }
    }

    @Override
    public String toString() {
        return "ClientRequestModel{" +
                "Result=" + Result +
                ", Type='" + Type + '\'' +
                ", MethodId='" + MethodId + '\'' +
                ", Params=" + Arrays.toString(Params) +
                ", Id='" + Id + '\'' +
                ", Service='" + Service + '\'' +
                '}';
    }
}
