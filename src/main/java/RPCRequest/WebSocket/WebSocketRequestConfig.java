package RPCRequest.WebSocket;

import Core.Model.RPCTypeConfig;
import RPCRequest.Abstract.RequestConfig;

/**
 * @ProjectName: YiXian_Client
 * @Package: com.yixian.material.RPC
 * @ClassName: RPCNetRequestConfig
 * @Description: java类作用描述
 * @Author: Jianxian
 * @CreateDate: 2021/3/5 18:07
 * @UpdateUser: Jianxian
 * @UpdateDate: 2021/3/5 18:07
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class WebSocketRequestConfig extends RequestConfig {
    private RPCTypeConfig type;
    private int timeout = -1;

    public WebSocketRequestConfig(RPCTypeConfig type){
        super(type);
        this.type = type;
    }

    public RPCTypeConfig getType() {
        return type;
    }

    public void setType(RPCTypeConfig type) {
        this.type = type;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
