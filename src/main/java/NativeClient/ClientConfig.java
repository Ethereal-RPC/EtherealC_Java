package NativeClient;

import NativeClient.Interface.IConnectSuccess;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientConfig {
    private int bufferSize = 1024;
    private int maxBufferSize = 10240;
    private Charset charset = StandardCharsets.UTF_8;
    private int dynamicAdjustBufferCount = -1;
    private boolean nettyAdaptBuffer = true;

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
