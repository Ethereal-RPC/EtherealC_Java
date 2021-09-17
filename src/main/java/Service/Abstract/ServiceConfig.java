package Service.Abstract;

import Core.Model.AbstractTypes;
import Service.Interface.IServiceConfig;

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
    private AbstractTypes types;

    public AbstractTypes getTypes() {
        return types;
    }

    public void setTypes(AbstractTypes types) {
        this.types = types;
    }

    public ServiceConfig(AbstractTypes type) {
        this.types = type;
    }
}
