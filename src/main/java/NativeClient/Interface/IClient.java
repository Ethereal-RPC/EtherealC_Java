package NativeClient.Interface;

import Core.Interface.IExceptionEvent;
import Core.Interface.ILogEvent;
import Core.Model.ClientRequestModel;

/**
 * @ProjectName: YiXian
 * @Package: com.xianyu.yixian.Core.Model
 * @ClassName: EchoClient
 * @Description: TCP客户端
 * @Author: Jianxian
 * @CreateDate: 2020/11/16 20:17
 * @UpdateUser: Jianxian
 * @UpdateDate: 2020/11/16 20:17
 * @UpdateRemark: 类的第一次生成
 * @Version: 1.0
 */
public interface IClient extends IExceptionEvent, ILogEvent {
    void connect();
    void disConnect();
    boolean isConnect();
    boolean sendClientRequestModel(ClientRequestModel request);
}
