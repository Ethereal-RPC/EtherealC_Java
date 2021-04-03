package RPCNet;

import RPCNet.Interface.IClientRequestSend;
import RPCNet.Interface.IClientResponseReceive;
import RPCNet.Interface.IServerRequestReceive;

/**
 * @ProjectName: YiXian_Client
 * @Package: com.yixian.material.RPC
 * @ClassName: RPCNetConfig
 * @Description: java类作用描述
 * @Author: Jianxian
 * @CreateDate: 2021/3/5 18:10
 * @UpdateUser: Jianxian
 * @UpdateDate: 2021/3/5 18:10
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class NetConfig {
    private IServerRequestReceive serverRequestReceive;
    private IClientResponseReceive clientResponseReceive;
    private IClientRequestSend clientRequestSend;
    private boolean Debug = false;

    public boolean isDebug() {
        return Debug;
    }

    public void setDebug(boolean debug) {
        Debug = debug;
    }

    public IServerRequestReceive getServerRequestReceive() {
        return serverRequestReceive;
    }

    public void setServerRequestReceive(IServerRequestReceive serverRequestReceive) {
        this.serverRequestReceive = serverRequestReceive;
    }

    public IClientResponseReceive getClientResponseReceive() {
        return clientResponseReceive;
    }

    public void setClientResponseReceive(IClientResponseReceive clientResponseReceive) {
        this.clientResponseReceive = clientResponseReceive;
    }

    public IClientRequestSend getClientRequestSend() {
        return clientRequestSend;
    }

    public void setClientRequestSend(IClientRequestSend clientRequestSend) {
        this.clientRequestSend = clientRequestSend;
    }
}
