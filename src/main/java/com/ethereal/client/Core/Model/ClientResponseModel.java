package com.ethereal.client.Core.Model;

import com.google.gson.annotations.Expose;

public class ClientResponseModel {
    @Expose
    private String type = "ER-1.0-ClientResponse";
    @Expose
    private String result = null;
    @Expose
    private Error error = null;
    @Expose
    private String id = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ClientResponseModel{" +
                "type='" + type + '\'' +
                ", result='" + result + '\'' +
                ", error=" + error +
                ", id='" + id + '\'' +
                '}';
    }
}
