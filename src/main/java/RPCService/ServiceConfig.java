package RPCService;

import Model.RPCType;
/**
 * @ProjectName: YiXian_Client
 * @Package: com.yixian.material.RPC
 * @ClassName: RPCNetServiceConfig
 * @Description: java类作用描述
 * @Author: Jianxian
 * @CreateDate: 2021/3/5 17:47
 * @UpdateUser: Jianxian
 * @UpdateDate: 2021/3/5 17:47
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ServiceConfig {
    private Boolean tokenEnable;
    private RPCType type;

    public Boolean getTokenEnable() {
        return tokenEnable;
    }

    public void setTokenEnable(Boolean tokenEnable) {
        this.tokenEnable = tokenEnable;
    }

    public RPCType getType() {
        return type;
    }

    public void setType(RPCType type) {
        this.type = type;
    }

    public ServiceConfig(RPCType type) {
        this.type = type;
    }
}
