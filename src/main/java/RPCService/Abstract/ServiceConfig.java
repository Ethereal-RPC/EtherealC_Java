package RPCService.Abstract;

import Core.Model.RPCTypeConfig;
import RPCService.Interface.IServiceConfig;

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
public class ServiceConfig implements IServiceConfig {
    private RPCTypeConfig types;

    public RPCTypeConfig getTypes() {
        return types;
    }

    public void setTypes(RPCTypeConfig types) {
        this.types = types;
    }

    public ServiceConfig(RPCTypeConfig type) {
        this.types = type;
    }
}
