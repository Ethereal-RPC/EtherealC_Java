package RPCNet.NetNode.Model;

import com.google.gson.annotations.Expose;

public class NetNode {
    /// <summary>
    /// Net节点名
    /// </summary>
    @Expose
    private String name;
    /// <summary>
    /// 连接数量
    /// </summary>
    @Expose
    private long connects;
    /// <summary>
    /// ip地址
    /// </summary>
    @Expose
    private String ip;
    /// <summary>
    /// port地址
    /// </summary>
    @Expose
    private String port;
    /// <summary>
    /// 硬件信息
    /// </summary>
    @Expose
    private HardwareInformation hardwareInformation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getConnects() {
        return connects;
    }

    public void setConnects(long connects) {
        this.connects = connects;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public HardwareInformation getHardwareInformation() {
        return hardwareInformation;
    }

    public void setHardwareInformation(HardwareInformation hardwareInformation) {
        this.hardwareInformation = hardwareInformation;
    }
}
