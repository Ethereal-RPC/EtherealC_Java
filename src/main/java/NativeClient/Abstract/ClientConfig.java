package NativeClient.Abstract;

import Core.Model.ClientRequestModel;
import Core.Model.ClientResponseModel;
import Core.Model.ServerRequestModel;
import NativeClient.Delegate.ClientRequestModelSerializeDelegate;
import NativeClient.Delegate.ClientResponseModelDeserializeDelegate;
import NativeClient.Delegate.ServerRequestModelDeserializeDelegate;
import Utils.Utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class ClientConfig {
    private Charset charset = StandardCharsets.UTF_8;
    private ClientRequestModelSerializeDelegate clientRequestModelSerialize;
    private ServerRequestModelDeserializeDelegate serverRequestModelDeserialize;
    private ClientResponseModelDeserializeDelegate clientResponseModelDeserialize;


    public ClientConfig(){
        //模型=>类=>实例化类（实体)=>数据(字节、文本）【序列化】=>发送
        clientRequestModelSerialize = obj -> Utils.gson.toJson(obj,ClientRequestModel.class);
        serverRequestModelDeserialize = obj -> Utils.gson.fromJson(obj,ServerRequestModel.class);
        clientResponseModelDeserialize = obj -> Utils.gson.fromJson(obj,ClientResponseModel.class);
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
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

}
