package com.ethereal.client.Net.Abstract;

import com.ethereal.client.Core.BaseCore.BaseCore;
import com.ethereal.client.Core.Enums.NetType;
import com.ethereal.client.Core.EventRegister.ExceptionEvent;
import com.ethereal.client.Core.EventRegister.LogEvent;
import com.ethereal.client.Core.Model.*;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Net.Interface.INet;
import com.ethereal.client.Request.Abstract.Request;

import java.util.HashMap;

public abstract class Net extends BaseCore implements INet {
    protected NetConfig config;
    protected String name;
    protected NetType netType;
    //Java没有自带三元组，这里就引用Kotlin了.
    protected HashMap<String, Request> requests = new HashMap<>();


    public Net(String name){
        this.name = name;
    }
    public NetType getNetType() {
        return netType;
    }

    public void setNetType(NetType netType) {
        this.netType = netType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public NetConfig getConfig() {
        return config;
    }

    public void setConfig(NetConfig config) {
        this.config = config;
    }
    public HashMap<String, Request> getRequests() {
        return requests;
    }

    public void setRequests(HashMap<String, Request> requests) {
        this.requests = requests;
    }

}
