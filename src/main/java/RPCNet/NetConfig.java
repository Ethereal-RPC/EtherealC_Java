package RPCNet;

import NativeClient.ClientConfig;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.javatuples.Tuple;

import java.util.List;

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
    /// <summary>
    /// 分布式模式是否开启
    /// </summary>
    private Boolean netNodeMode = false;
    /// <summary>
    /// 分布式IP组
    /// </summary>
    private List<Pair<String, ClientConfig>> netNodeIps;
    /// <summary>
    /// 服务注册心跳间隔
    /// </summary>
    private int netNodeHeartInterval = 6000;

    public Boolean getNetNodeMode() {
        return netNodeMode;
    }

    public void setNetNodeMode(Boolean netNodeMode) {
        this.netNodeMode = netNodeMode;
    }

    public List<Pair<String, ClientConfig>> getNetNodeIps() {
        return netNodeIps;
    }

    public void setNetNodeIps(List<Pair<String, ClientConfig>> netNodeIps) {
        this.netNodeIps = netNodeIps;
    }

    public int getNetNodeHeartInterval() {
        return netNodeHeartInterval;
    }

    public void setNetNodeHeartInterval(int netNodeHeartInterval) {
        this.netNodeHeartInterval = netNodeHeartInterval;
    }
}
