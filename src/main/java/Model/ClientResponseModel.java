package Model;

import com.google.gson.annotations.Expose;

public class ClientResponseModel {
    @Expose
    private String JsonRpc = null;
    @Expose
    private String Result = null;
    @Expose
    private String ResultType = null;
    @Expose
    private Error error = null;
    @Expose
    private String Id = null;
    @Expose
    private String service = null;
    public String getJsonRpc() {
        return JsonRpc;
    }

    public void setJsonRpc(String jsonRpc) {
        JsonRpc = jsonRpc;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public String getResultType() {
        return ResultType;
    }

    public void setResultType(String resultType) {
        ResultType = resultType;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "ClientResponseModel{" +
                "JsonRpc='" + JsonRpc + '\'' +
                ", Result='" + Result + '\'' +
                ", ResultType='" + ResultType + '\'' +
                ", error=" + error +
                ", Id='" + Id + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}
