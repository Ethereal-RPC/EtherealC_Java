package NativeClient;

import Model.ClientRequestModel;
import Model.ClientResponseModel;
import Model.ServerRequestModel;
import NativeClient.Interface.IConnectSuccess;
import Utils.Utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientConfig {
    public interface IClientRequestModelSerialize {
        String Serialize(ClientRequestModel obj);
    }
    public interface IServerRequestModelDeserialize {
        ServerRequestModel Deserialize(String obj);
    }
    public interface IClientResponseModelDeserialize {
        ClientResponseModel Deserialize(String obj);
    }
    private int bufferSize = 1024;
    private int maxBufferSize = 10240;
    private Charset charset = StandardCharsets.UTF_8;
    private int dynamicAdjustBufferCount = 1;
    private boolean nettyAdaptBuffer = false;
    private IClientRequestModelSerialize clientRequestModelSerialize;
    private IServerRequestModelDeserialize serverRequestModelDeserialize;
    private IClientResponseModelDeserialize clientResponseModelDeserialize;

    public ClientConfig(){
        clientRequestModelSerialize = obj -> Utils.gson.toJson(obj,ClientRequestModel.class);
        serverRequestModelDeserialize = obj -> Utils.gson.fromJson(obj,ServerRequestModel.class);
        clientResponseModelDeserialize = obj -> Utils.gson.fromJson(obj,ClientResponseModel.class);
    }

    public IClientRequestModelSerialize getClientRequestModelSerialize() {
        return clientRequestModelSerialize;
    }

    public void setClientRequestModelSerialize(IClientRequestModelSerialize clientRequestModelSerialize) {
        this.clientRequestModelSerialize = clientRequestModelSerialize;
    }

    public IServerRequestModelDeserialize getServerRequestModelDeserialize() {
        return serverRequestModelDeserialize;
    }

    public void setServerRequestModelDeserialize(IServerRequestModelDeserialize serverRequestModelDeserialize) {
        this.serverRequestModelDeserialize = serverRequestModelDeserialize;
    }

    public IClientResponseModelDeserialize getClientResponseModelDeserialize() {
        return clientResponseModelDeserialize;
    }

    public void setClientResponseModelDeserialize(IClientResponseModelDeserialize clientResponseModelDeserialize) {
        this.clientResponseModelDeserialize = clientResponseModelDeserialize;
    }

    public boolean isNettyAdaptBuffer() {
        return nettyAdaptBuffer;
    }

    public void setNettyAdaptBuffer(boolean nettyAdaptBuffer) {
        this.nettyAdaptBuffer = nettyAdaptBuffer;
    }

    public int getDynamicAdjustBufferCount() {
        return dynamicAdjustBufferCount;
    }

    public void setDynamicAdjustBufferCount(int dynamicAdjustBufferCount) {
        this.dynamicAdjustBufferCount = dynamicAdjustBufferCount;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    private IConnectSuccess connectSuccess;

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public IConnectSuccess getConnectSuccess() {
        return connectSuccess;
    }

    public void setConnectSuccess(IConnectSuccess connectSuccess) {
        this.connectSuccess = connectSuccess;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }
}
