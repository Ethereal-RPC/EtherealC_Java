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
    private boolean nettyAdaptBuffer = false;
    private ClientRequestModelSerializeDelegate clientRequestModelSerialize;
    private ServerRequestModelDeserializeDelegate serverRequestModelDeserialize;
    private ClientResponseModelDeserializeDelegate clientResponseModelDeserialize;
    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();

    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }

    public LogEvent getLogEvent() {
        return logEvent;
    }


    public ClientConfig(){
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

    private ConnectSuccessDelegate connectSuccess;

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public ConnectSuccessDelegate getConnectSuccess() {
        return connectSuccess;
    }

    public void setConnectSuccess(ConnectSuccessDelegate connectSuccess) {
        this.connectSuccess = connectSuccess;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

    public void onException(RPCException.ErrorCode code, String message, SocketClient client) throws RPCException {
        onException(new RPCException(code,message),client);
    }
    public void onException(RPCException exception, SocketClient client) throws RPCException {
        exceptionEvent.OnEvent(exception,client);
        throw exception;
    }

    public void onLog(RPCLog.LogCode code, String message, SocketClient client){
        onLog(new RPCLog(code,message),client);
    }
    public void onLog(RPCLog log, SocketClient client){
        logEvent.OnEvent(log,client);
    }
}
