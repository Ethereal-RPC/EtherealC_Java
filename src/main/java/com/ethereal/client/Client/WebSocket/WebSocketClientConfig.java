package com.ethereal.client.Client.WebSocket;

import com.ethereal.client.Client.Abstract.ClientConfig;

public class WebSocketClientConfig extends ClientConfig {
    protected int maxBufferSize = 10240;
    protected int threadCount = 5;
    protected boolean isSyncConnect = false;

    public boolean isSyncConnect() {
        return isSyncConnect;
    }

    public void setSyncConnect(boolean syncConnect) {
        isSyncConnect = syncConnect;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(int maxBufferSize) {
        this.maxBufferSize = maxBufferSize;
    }

}
