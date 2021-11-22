package com.ethereal.client.Client.WebSocket;

import com.ethereal.client.Core.Model.ClientResponseModel;
import com.ethereal.client.Core.Model.TrackException;
import com.ethereal.client.Core.Model.TrackLog;
import com.ethereal.client.Core.Model.ServerRequestModel;
import com.ethereal.client.Net.Abstract.Net;
import com.ethereal.client.Net.NetCore;
import com.ethereal.client.Service.Abstract.Service;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

public class CustomWebSocketHandler extends SimpleChannelInboundHandler<Object> {
    private WebSocketClient client;
    private ChannelPromise handshakeFuture;
    private WebSocketClientHandshaker handshaker;

    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }

    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }

    public WebSocketClientHandshaker getHandshaker() {
        return handshaker;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public CustomWebSocketHandler(WebSocketClient client,WebSocketClientHandshaker handshaker){
        this.handshaker = handshaker;
        this.client = client;
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        client.es.execute(()->client.onDisConnect());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        try{
            Channel ch = ctx.channel();
            if (!handshaker.isHandshakeComplete()) {
                try {
                    handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                    client.onLog(TrackLog.LogCode.Runtime,"WebSocket com.ethereal.client.Client connected!");
                    handshakeFuture.setSuccess();
                    client.onConnectSuccess();
                } catch (WebSocketHandshakeException e) {
                    handshakeFuture.setFailure(e);
                    throw new TrackException(TrackException.ErrorCode.Runtime,"WebSocket com.ethereal.client.Client failed to connect");
                }
                return;
            }

            if (msg instanceof FullHttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;
                throw new IllegalStateException("Unexpected FullHttpResponse (content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
            }

            if (msg instanceof TextWebSocketFrame) {
                String data = ((TextWebSocketFrame) msg).content().toString(client.getConfig().getCharset());
                JsonObject json_object = JsonParser.parseString(data).getAsJsonObject();
                if(json_object.get("Type").toString().equals("ER-1.0-ServerRequest")){
                    client.onLog(TrackLog.LogCode.Runtime,"[服-请求]:" + data);
                    //服务器模型的反序列化 实体
                    ServerRequestModel serverRequestModel = client.getConfig().getServerRequestModelDeserialize().Deserialize(data);
                    client.es.execute(()->{
                        try {
                            Service service = client.getRequest().getServices().get(serverRequestModel.getService());
                            if(service != null){
                                service.serverRequestReceiveProcess(serverRequestModel);
                            }
                            else {
                                throw new TrackException(TrackException.ErrorCode.Runtime,String.format("%s-%s Not Found",client.getRequest().getName(),serverRequestModel.getService()));
                            }
                        } catch (java.lang.Exception e) {
                            client.onException(new TrackException(e));
                        }
                    });
                }
                else {
                    client.onLog(TrackLog.LogCode.Runtime,"[客-返回]:" + data);
                    ClientResponseModel clientResponseModel = client.getConfig().getClientResponseModelDeserialize().Deserialize(data);
                    client.es.execute(()->{
                        try {
                            client.getRequest().clientResponseProcess(clientResponseModel);
                        } catch (java.lang.Exception e) {
                            client.onException(new TrackException(e));
                        }
                    });
                }
            } else if (msg instanceof PongWebSocketFrame) {

            } else if (msg instanceof CloseWebSocketFrame) {
                client.disConnect();
            }
        }
        catch (java.lang.Exception e){
            client.onException(new TrackException(e));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        client.onException(TrackException.ErrorCode.NotEthereal,cause.getMessage());
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
