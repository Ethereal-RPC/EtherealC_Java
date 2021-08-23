package RPCService;

import Model.RPCException;
import Model.RPCLog;
import Model.RPCTypeConfig;
import RPCService.Event.ExceptionEvent;
import RPCService.Event.LogEvent;

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
