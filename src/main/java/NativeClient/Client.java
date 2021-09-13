package NativeClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Model.ClientRequestModel;
import Model.RPCException;
import Model.RPCLog;
import NativeClient.Event.DisConnectEvent;
import NativeClient.Event.ConnectEvent;
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
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateHandler;

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
public class Client {
    private Random random = new Random();
    private ClientConfig config;
    private String netName;
    private String serviceName;
    private String prefixes;
    private ChannelFuture channelFuture;
    private ExceptionEvent exceptionEvent = new ExceptionEvent();
    private LogEvent logEvent = new LogEvent();
    private ConnectEvent connectEvent = new ConnectEvent();
    private DisConnectEvent disConnectEvent = new DisConnectEvent();
    Bootstrap bootstrap;
    private ExecutorService es;

    public ConnectEvent getConnectEvent() {
        return connectEvent;
    }

    public void setConnectEvent(ConnectEvent connectEvent) {
        this.connectEvent = connectEvent;
    }

    public DisConnectEvent getDisConnectEvent() {
        return disConnectEvent;
    }

    public void setDisConnectEvent(DisConnectEvent disConnectEvent) {
        this.disConnectEvent = disConnectEvent;
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

    public Client(String netName, String serviceName, String prefixes, ClientConfig config) {
        this.config = config;
        this.netName = netName;
        this.serviceName = serviceName;
        this.prefixes = prefixes;
        es = Executors.newFixedThreadPool(config.getThreadCount());
    }

    public String getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(String prefixes) {
        this.prefixes = prefixes;
    }

    public ClientConfig getConfig() {
        return config;
    }

    public void setConfig(ClientConfig config) {
        this.config = config;
    }

    public ExecutorService getEs() {
        return es;
    }

    public void setEs(ExecutorService es) {
        this.es = es;
    }

    public void start() {

        if (channelFuture != null) {
            disConnect();
            channelFuture = null;
        }
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            URI uri = new URI("ws://" + prefixes);
            CustomWebSocketHandler webSocketHandler = new CustomWebSocketHandler(this,WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));
            CustomHeartbeatHandler customWebSocketHandler = new CustomHeartbeatHandler(this);
            bootstrap = new Bootstrap();               //1
            bootstrap.group(group)                                //2
                    .channel(NioSocketChannel.class)            //3
                    .handler(new ChannelInitializer<SocketChannel>() {    //5
                        @Override
                        public void initChannel(SocketChannel ch) {
                            //数据处理
                            ch.pipeline().addLast(new HttpClientCodec());
                            ch.pipeline().addLast(new HttpObjectAggregator(config.getMaxBufferSize()));
                            ch.pipeline().addLast(webSocketHandler);
                            //心跳包
                            ch.pipeline().addLast(new IdleStateHandler(0,0,5));
                            ch.pipeline().addLast(customWebSocketHandler);
                        }
                    });
            channelFuture = bootstrap.connect(uri.getHost(),uri.getPort()).sync();
            webSocketHandler.getHandshakeFuture().sync();
        }
        catch (Exception e){
            onException(e);
            group.shutdownGracefully();
            onDisConnectEvent();
        }
    }

    public boolean send(ClientRequestModel request) {
        if(channelFuture.channel() !=null && channelFuture.channel().isActive()){
            String json = config.getClientRequestModelSerialize().Serialize(request);
            //多转一次格式，用户可能使用非Config的编码.
            json = new String(json.getBytes(config.getCharset()));
            channelFuture.channel().writeAndFlush(new TextWebSocketFrame(json));
            return true;
        }
        return false;
    }

    public void disConnect()  {
        try {
            this.channelFuture.channel().writeAndFlush(new CloseWebSocketFrame());
            this.channelFuture.channel().closeFuture();
            onDisConnectEvent();
        } catch (Exception e) {
            onException(e);
        }
    }

    public boolean isConnect(){
        if(channelFuture != null && channelFuture.channel() != null){
            return channelFuture.channel().isActive();
        }
        return false;
    }

    public void onException(RPCException.ErrorCode code, String message){
        onException(new RPCException(code, message));
    }

    public void onException(Exception exception)  {
        exceptionEvent.onEvent(exception, this);
    }

    public void onLog(RPCLog.LogCode code, String message) {
        onLog(new RPCLog(code, message));
    }

    public void onLog(RPCLog log) {
        logEvent.onEvent(log, this);
    }

    public void onConnectSuccess() {
        es.submit(()->connectEvent.onEvent(this));
    }

    public void onDisConnectEvent() {
        es.submit(()->disConnectEvent.onEvent(this));
    }


}
