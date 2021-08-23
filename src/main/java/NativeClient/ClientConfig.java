package NativeClient;

import Model.*;
import NativeClient.Event.ExceptionEvent;
import NativeClient.Event.LogEvent;
import NativeClient.Interface.*;
import Utils.Utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientConfig {

    private int bufferSize = 1024;
    private int maxBufferSize = 10240;
    private Charset charset = StandardCharsets.UTF_8;
    private int dynamicAdjustBufferCount = 1;
    private boolean nettyAdaptBuffer = false;//是否开启动态缓冲池
    private ClientRequestModelSerializeDelegate clientRequestModelSerialize;
    private ServerRequestModelDeserializeDelegate serverRequestModelDeserialize;
    private ClientResponseModelDeserializeDelegate clientResponseModelDeserialize;


    public ClientConfig(){
        //模型=>类=>实例化类（实体)=>数据(字节、文本）【序列化】=>发送
        clientRequestModelSerialize = obj -> Utils.gson.toJson(obj,ClientRequestModel.class);
        serverRequestModelDeserialize = obj -> Utils.gson.fromJson(obj,ServerRequestModel.class);
        clientResponseModelDeserialize = obj -> Utils.gson.fromJson(obj,ClientResponseModel.class);
    }

    public ClientRequestModelSerializeDelegate getClientRequestModelSerialize() {
        return clientRequestModelSerialize;
    }

    public void setClientRequestModelSerialize(ClientRequestModelSerializeDelegate clientRequestModelSerialize) {
        this.clientRequestModelSerialize = clientRequestModelSerialize;
    }

    public ServerRequestModelDeserializeDelegate getServerRequestModelDeserialize() {
        return serverRequestModelDeserialize;
    }

    public void setServerRequestModelDeserialize(ServerRequestModelDeserializeDelegate serverRequestModelDeserialize) {
        this.serverRequestModelDeserialize = serverRequestModelDeserialize;
    }

    public ClientResponseModelDeserializeDelegate getClientResponseModelDeserialize() {
        return clientResponseModelDeserialize;
    }

    public void setClientResponseModelDeserialize(ClientResponseModelDeserializeDelegate clientResponseModelDeserialize) {
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


    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

}
