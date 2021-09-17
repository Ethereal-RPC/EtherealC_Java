package Client.WebSocket;

import Core.Model.ClientRequestModel;
import Core.Model.RPCException;
import Client.Abstract.Client;
import Client.Abstract.ClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
public class WebSocketClient extends Client {
    protected String prefixes;
    protected ChannelFuture channelFuture;
    protected boolean isDisConnect = false;
    protected Bootstrap bootstrap;
    protected ExecutorService es;

    public WebSocketClient(String netName, String serviceName, String prefixes, ClientConfig config) {
        this.config = config;
        this.netName = netName;
        this.serviceName = serviceName;
        this.prefixes = prefixes;
        this.es=Executors.newFixedThreadPool(getConfig().threadCount);
    }
    public ExecutorService getEs() {
        return es;
    }
    public void setEs(ExecutorService es) {
        this.es = es;
    }
    public String getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(String prefixes) {
        this.prefixes = prefixes;
    }

    public WebSocketClientConfig getConfig() {
        return (WebSocketClientConfig)config;
    }

    public void setConfig(ClientConfig config) {
        this.config = config;
    }
    @Override
    public void connect() {
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
                            ch.pipeline().addLast(new HttpObjectAggregator(getConfig().getMaxBufferSize()));
                            ch.pipeline().addLast(webSocketHandler);
                            //心跳包
                            ch.pipeline().addLast(new IdleStateHandler(0,0,5));
                            ch.pipeline().addLast(customWebSocketHandler);
                        }
                    });
            if(getConfig().isSyncConnect){
                channelFuture = bootstrap.connect(uri.getHost(),uri.getPort());
                webSocketHandler.getHandshakeFuture();
            }
            else {
                channelFuture = bootstrap.connect(uri.getHost(),uri.getPort()).sync();
                webSocketHandler.getHandshakeFuture().sync();
            }
        }
        catch (Exception e){
            onException(new RPCException(e));
            group.shutdownGracefully();
            onDisConnectEvent();
        }
    }
    @Override
    public boolean sendClientRequestModel(ClientRequestModel request) {
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
        boolean temp = isDisConnect;
        try {
            if(!isDisConnect){
                isDisConnect = true;
                this.channelFuture.channel().writeAndFlush(new CloseWebSocketFrame());
                this.channelFuture.channel().closeFuture();
            }
        } catch (Exception e) {
            onException(new RPCException(e));
        }
        finally {
            if(!temp){
                //es.execute(this::onDisConnectEvent);
                onDisConnectEvent();
            }
        }
    }

    public boolean isConnect(){
        if(channelFuture != null && channelFuture.channel() != null){
            return true;
        }
        return false;
    }
    @Override
    public void onConnectSuccess() {
        es.execute(()->connectEvent.onEvent(this));
    }
    @Override
    public void onDisConnectEvent() {
        es.execute(()->disConnectEvent.onEvent(this));
    }
}
