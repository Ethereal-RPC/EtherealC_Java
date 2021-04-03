package NativeClient;

import NativeClient.Interface.IConnectSuccess;

public class ClientConfig {
    private int bufferSize = 1024;
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
}
