package com.ethereal.client.Core.Model;

import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.HashMap;

public class ClientRequestModel {
    @Expose(serialize = false,deserialize = false)
    private ClientResponseModel Result;
    @Expose
    private String Type = "ER-1.0-ClientRequest";
    @Expose
    private String Mapping;
    @Expose
    private HashMap<String ,String > Params;
    @Expose
    private String Id;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getMapping() {
        return Mapping;
    }

    public void setMapping(String mapping) {
        Mapping = mapping;
    }

    public HashMap<String, String> getParams() {
        return Params;
    }

    public void setParams(HashMap<String, String> params) {
        Params = params;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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
                ", Mapping='" + Mapping + '\'' +
                ", Id='" + Id + '\'' +
                '}';
    }
}
