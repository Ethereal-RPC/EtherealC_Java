package Service.Abstract;

import Core.Model.AbstractTypeGroup;
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
    private AbstractTypeGroup types;

    public AbstractTypeGroup getTypes() {
        return types;
    }

    public void setTypes(AbstractTypeGroup types) {
        this.types = types;
    }

    public ServiceConfig(AbstractTypeGroup type) {
        this.types = type;
    }
}
