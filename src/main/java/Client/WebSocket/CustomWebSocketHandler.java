package Client.WebSocket;

import Core.Model.ClientResponseModel;
import Core.Model.TrackException;
import Core.Model.TrackLog;
import Core.Model.ServerRequestModel;
import Net.Abstract.Net;
import Net.NetCore;
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
        client.es.execute(()->client.onDisConnectEvent());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        try{
            Channel ch = ctx.channel();
            if (!handshaker.isHandshakeComplete()) {
                try {
                    handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                    client.onLog(TrackLog.LogCode.Runtime,"WebSocket Client connected!");
                    handshakeFuture.setSuccess();
                    this.client.onConnectSuccess();
                } catch (WebSocketHandshakeException e) {
                    handshakeFuture.setFailure(e);
                    throw new TrackException(TrackException.ErrorCode.Runtime,"WebSocket Client failed to connect");
                }
                return;
            }
            if (msg instanceof FullHttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;
                throw new IllegalStateException("Unexpected FullHttpResponse (content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
            }

            if (msg instanceof TextWebSocketFrame) {
                String data = ((TextWebSocketFrame) msg).content().toString(client.getConfig().getCharset());
                Net net = NetCore.get(client.getNetName());
                if(net == null){
                    throw new TrackException(TrackException.ErrorCode.Runtime,
                            String.format("未找到net", client.getNetName()));
                }
                JsonObject json_object = JsonParser.parseString(data).getAsJsonObject();
                if(json_object.get("Type").toString().equals("ER-1.0-ServerRequest")){
                    client.onLog(TrackLog.LogCode.Runtime,"[服-请求]:" + data);
                    //服务器模型的反序列化 实体
                    ServerRequestModel serverRequestModel = client.getConfig().getServerRequestModelDeserialize().Deserialize(data);
                    client.es.execute(()->{
                        try {
                            net.getServerRequestReceive().ServerRequestReceive(serverRequestModel);
                        } catch (java.lang.Exception e) {
                            net.onException(new TrackException(e));
                        }
                    });
                }
                else {
                    client.onLog(TrackLog.LogCode.Runtime,"[客-返回]:" + data);
                    ClientResponseModel clientResponseModel = client.getConfig().getClientResponseModelDeserialize().Deserialize(data);
                    client.es.execute(()->{
                        try {
                            net.getClientResponseReceive().ClientResponseReceive(clientResponseModel);
                        } catch (java.lang.Exception e) {
                            net.onException(new TrackException(e));
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
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
