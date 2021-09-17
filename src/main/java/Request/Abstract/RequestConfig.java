package Request.Abstract;

import Core.Model.AbstractTypes;
import Request.Interface.IRequestConfig;

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
public abstract class RequestConfig implements IRequestConfig {
    private AbstractTypes type;
    private int timeout = -1;

    public RequestConfig(AbstractTypes type){
        this.type = type;
    }

    public AbstractTypes getType() {
        return type;
    }

    public void setType(AbstractTypes type) {
        this.type = type;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
