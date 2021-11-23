package com.ethereal.client.Client.Abstract;

import com.ethereal.client.Client.EventRegister.ConnectFailEvent;
import com.ethereal.client.Core.BaseCore.BaseCore;
import com.ethereal.client.Core.EventRegister.ExceptionEvent;
import com.ethereal.client.Core.EventRegister.LogEvent;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.TrackLog;
import com.ethereal.client.Client.EventRegister.ConnectSuccessEvent;
import com.ethereal.client.Client.EventRegister.DisConnectEvent;
import com.ethereal.client.Client.Interface.IClient;
import com.ethereal.client.Request.Abstract.Request;

/**
 * @ProjectName: YiXian
 * @Package: com.xianyu.yixian.com.ethereal.client.Core.Model
 * @ClassName: EchoClient
 * @Description: TCP客户端
 * @Author: Jianxian
 * @CreateDate: 2020/11/16 20:17
 * @UpdateUser: Jianxian
 * @UpdateDate: 2020/11/16 20:17
 * @UpdateRemark: 类的第一次生成
 * @Version: 1.0
 */
public abstract class Client extends BaseCore implements IClient {
    protected ClientConfig config;
    protected String prefixes;
    protected Request request;
    protected ConnectSuccessEvent connectSuccessEvent = new ConnectSuccessEvent();
    protected ConnectFailEvent connectFailEvent = new ConnectFailEvent();
    protected DisConnectEvent disConnectEvent = new DisConnectEvent();
    public Client(String prefixes){
        this.prefixes = prefixes;
    }

    public String getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(String prefixes) {
        this.prefixes = prefixes;
    }

    public ConnectSuccessEvent getConnectSuccessEvent() {
        return connectSuccessEvent;
    }

    public void setConnectSuccessEvent(ConnectSuccessEvent connectSuccessEvent) {
        this.connectSuccessEvent = connectSuccessEvent;
    }

    public DisConnectEvent getDisConnectEvent() {
        return disConnectEvent;
    }

    public void setDisConnectEvent(DisConnectEvent disConnectEvent) {
        this.disConnectEvent = disConnectEvent;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public ClientConfig getConfig() {
        return config;
    }

    public void setConfig(ClientConfig config) {
        this.config = config;
    }

    public ConnectFailEvent getConnectFailEvent() {
        return connectFailEvent;
    }

    public void setConnectFailEvent(ConnectFailEvent connectFailEvent) {
        this.connectFailEvent = connectFailEvent;
    }

    public void onConnectSuccess() {
        connectSuccessEvent.onEvent(this);
    }
    public void onConnectFail() {
        connectFailEvent.onEvent(this);
    }
    public void onDisConnect() {
        disConnectEvent.onEvent(this);
    }
}
