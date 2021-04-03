package Model;

import com.google.gson.annotations.Expose;

import java.util.Arrays;

public class ServerRequestModel {
    @Expose
    public String jsonRpc;
    @Expose
    public String methodId;
    @Expose
    public Object[] params;
    @Expose
    public String service;


    public ServerRequestModel(String jsonRpc, String methodId, Object[] params, String service) {
        this.jsonRpc = jsonRpc;
        this.methodId = methodId;
        this.params = params;
        this.service = service;
    }

    public String getJsonRpc() {
        return jsonRpc;
    }

    public void setJsonRpc(String jsonRpc) {
        this.jsonRpc = jsonRpc;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
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
                "jsonRpc='" + jsonRpc + '\'' +
                ", methodId='" + methodId + '\'' +
                ", params=" + Arrays.toString(params) +
                ", service='" + service + '\'' +
                '}';
    }
}
