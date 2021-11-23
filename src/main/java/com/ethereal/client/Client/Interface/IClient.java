package com.ethereal.client.Client.Interface;

import com.ethereal.client.Core.Interface.IExceptionEvent;
import com.ethereal.client.Core.Interface.ILogEvent;
import com.ethereal.client.Core.Model.ClientRequestModel;

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
public interface IClient {
    void connect();
    void disConnect();
    boolean isConnect();
    boolean sendClientRequestModel(ClientRequestModel request);
}
