    package NativeClient;

import Model.ClientRequestModel;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import Model.RPCException;
import Model.RPCLog;
import NativeClient.Event.ConnectFailEvent;
import NativeClient.Event.ConnectSuccessEvent;
import NativeClient.Event.ExceptionEvent;
import NativeClient.Event.LogEvent;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.javatuples.Pair;

    /**
 * @ProjectName: YiXian
 * @Package: com.xianyu.yixian.Model
 * @ClassName: EchoClient
 * @Description: TCP客户端
 * @Author: Jianxian
 * @CreateDate: 2020/11/16 20:17
 * @UpdateUser: Jianxian
 * @UpdateDate: 2020/11/16 20:17
 * @UpdateRemark: 类的第一次生成
 * @Version: 1.0
 */
public class SocketClient {

    private Channel channel;
    private Bootstrap bootstrap;

    private Random random = new Random();

    private ClientConfig config;
    private String netName;
    private String serviceName;
    private Pair<String,String> clientKey;

    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();
    private ConnectSuccessEvent connectSuccessEvent = new ConnectSuccessEvent();
    private ConnectFailEvent connectFailEvent = new ConnectFailEvent();

    public Channel getChannel() {
        return channel;
    }
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ConnectSuccessEvent getConnectSuccessEvent() {
        return connectSuccessEvent;
    }
    public void setConnectSuccessEvent(ConnectSuccessEvent connectSuccessEvent) {
        this.connectSuccessEvent = connectSuccessEvent;
    }
    public ConnectFailEvent getConnectFailEvent() {
        return connectFailEvent;
    }
    public void setConnectFailEvent(ConnectFailEvent connectFailEvent) {
        this.connectFailEvent = connectFailEvent;
    }

    public ExceptionEvent getExceptionEvent() {
        return exceptionEvent;
    }
    public void setExceptionEvent(ExceptionEvent exceptionEvent) {
        this.exceptionEvent = exceptionEvent;
    }
    public LogEvent getLogEvent() {
        return logEvent;
    }
    public void setLogEvent(LogEvent logEvent) {
        this.logEvent = logEvent;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public String getNetName() {
        return netName;
    }

    public void setNetName(String netName) {
        this.netName = netName;
    }

    public SocketClient(String netName,String serviceName,Pair<String,String> clientKey, ClientConfig config) {
        this.config = config;
        this.netName = netName;
        this.serviceName = serviceName;
        this.clientKey = clientKey;
    }   
    public Pair<String, String> getClientKey() {
        return clientKey;
    }
    public void setClientKey(Pair<String, String> clientKey) {
        this.clientKey = clientKey;
    }
    public ClientConfig getConfig() {
        return config;
    }
    public void setConfig(ClientConfig config) {
        this.config = config;
    }

    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        try {
            bootstrap = new Bootstrap();               //1
            bootstrap.group(group)                                //2
                    .channel(NioSocketChannel.class)            //3
                    .handler(new ChannelInitializer<SocketChannel>() {    //5
                        @Override
                        public void initChannel(SocketChannel ch) {
                            //数据先流向第一个 解码器Decoder粘包
                            ch.pipeline().addLast(new CustomDecoder(netName,serviceName,clientKey,config));
                            //第二个 心跳包
                            ch.pipeline().addLast(new IdleStateHandler(0,0,5));
                            ch.pipeline().addLast(new CustomHeartbeatHandler(SocketClient.this));
                        }
                    });
            if(config.isNettyAdaptBuffer()){
                bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator());//开启动态缓冲池
            }
            else bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,new FixedRecvByteBufAllocator(config.getBufferSize()));//不开
            doConnect();
        }
        catch (Exception e){
            onConnectFailEvent();
            group.shutdownGracefully();
        }
    }

    protected void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = bootstrap.connect(clientKey.getValue0(),Integer.parseInt(clientKey.getValue1()));
        //监听 Listener
        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                channel = futureListener.channel();
                onConnectSuccess();
                onLog(RPCLog.LogCode.Runtime,"Connect to server successfully!");
            }
            else {
                onException(RPCException.ErrorCode.Runtime,"Failed to connect to server, try connect after 10s");
                futureListener.channel().eventLoop().schedule(() -> doConnect(), 10, TimeUnit.SECONDS);
            }
        });
    }
    public void disconnect() {
        channel.disconnect();
        channel.close();
    }
    public boolean send(ClientRequestModel request) {
        if(channel!=null && channel.isActive()){
            //封装  Head头包 Body数据
            byte[] body =  config.getClientRequestModelSerialize().Serialize(request).getBytes(config.getCharset());
            int dataLength = body.length;  //读取消息的长度
            byte[] b = new byte[4];
            b[0] = (byte) (dataLength & 0xff);
            b[1] = (byte) (dataLength >> 8 & 0xff);
            b[2] = (byte) (dataLength >> 16 & 0xff);
            b[3] = (byte) (dataLength >> 24 & 0xff);
            byte[] pattern;
            pattern = new byte[]{0};
            byte[] future = new byte[27];
            ByteBuf out = new UnpooledHeapByteBuf(ByteBufAllocator.DEFAULT,32 + body.length,32 + body.length);
            out.writeBytes(b);
            out.writeBytes(pattern);
            out.writeBytes(future);
            out.writeBytes(body);  //消息体中包含我们要发送的数据
            channel.writeAndFlush(out);
            return true;
        }
        return false;
    }


    public void onException(RPCException.ErrorCode code, String message) throws Exception {
        onException(new RPCException(code,message));
    }

    public void onException(Exception exception) throws Exception {
        exceptionEvent.onEvent(exception,this);
        throw exception;
    }

    public void onLog(RPCLog.LogCode code, String message){
        onLog(new RPCLog(code,message));
    }

    public void onLog(RPCLog log){
        logEvent.onEvent(log,this);
    }

    public void onConnectSuccess()  {
        connectSuccessEvent.onEvent(this);
    }
    public void onConnectFailEvent() throws Exception {
        connectFailEvent.onEvent(this);
    }
}
