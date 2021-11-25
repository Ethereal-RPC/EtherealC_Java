package com.ethereal.client.Client.WebSocket;

import com.ethereal.client.Core.Model.ClientRequestModel;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Client.Abstract.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
public class WebSocketClient extends Client {
    protected ChannelFuture channelFuture;
    protected boolean isDisConnect = false;
    protected Bootstrap bootstrap;
    protected ExecutorService es;

    public WebSocketClient(String prefixes) {
        super(prefixes);
        this.config = new WebSocketClientConfig();
        this.es=Executors.newFixedThreadPool(getConfig().threadCount);
    }

    public ExecutorService getEs() {
        return es;
    }
    public void setEs(ExecutorService es) {
        this.es = es;
    }

    public WebSocketClientConfig getConfig() {
        return (WebSocketClientConfig)config;
    }

    @Override
    public void connect() {
        if (channelFuture != null) {
            disConnect();
            channelFuture = null;
        }
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            prefixes = prefixes.replace("ethereal://","ws://");
            if(prefixes.charAt(prefixes.length()- 1) != '/')prefixes += "/" + request.getName();
            else prefixes += request.getName();
            URI uri = new URI(prefixes);
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
                channelFuture = bootstrap.connect(uri.getHost(),uri.getPort()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(!future.isSuccess()){
                            onConnectFail();
                        }
                    }
                }).sync();
            }
            else {
                channelFuture = bootstrap.connect(uri.getHost(),uri.getPort()).addListener((ChannelFutureListener) future -> {
                    if(!future.isSuccess()){
                        onConnectFail();
                    }
                });
            }
        }
        catch (Exception e){
            onException(new TrackException(e));
        }
    }

    @Override
    public boolean sendClientRequestModel(ClientRequestModel request) {
        if(isConnect()){
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
                if(isConnect()){
                    this.channelFuture.channel().writeAndFlush(new CloseWebSocketFrame());
                    this.channelFuture.channel().closeFuture();
                }
            }
        } catch (java.lang.Exception e) {
            onException(new TrackException(e));
        }
        finally {
            if(!temp){
                onDisConnect();
            }
        }
    }

    public boolean isConnect(){
        return channelFuture != null && channelFuture.channel() != null && channelFuture.channel().isActive();
    }
    @Override
    public void onConnectSuccess() {
        es.execute(()-> connectSuccessEvent.onEvent(this));
    }
    @Override
    public void onDisConnect() {
        es.execute(()->disConnectEvent.onEvent(this));
    }

    @Override
    public void onConnectFail() {
        es.execute(()->connectFailEvent.onEvent(this));
    }
}
