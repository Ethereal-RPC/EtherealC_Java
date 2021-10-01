package com.ethereal.client.Core.Model;

import com.google.gson.annotations.Expose;

public class Error {
    public enum ErrorCode { Intercepted,NotFoundService,NotFoundMethod,NotFoundNet,BufferFlow,Common,MaxConnects }
    @Expose
    private ErrorCode Code;
    @Expose
    private String Message;
    @Expose
    private String Data;

    public ErrorCode getCode() {
        return Code;
    }

    public void setCode(ErrorCode code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    @Override
    public String toString() {
        return "Error{" +
                "Code=" + Code +
                ", Message='" + Message + '\'' +
                ", Data='" + Data + '\'' +
                '}';
    }
}
