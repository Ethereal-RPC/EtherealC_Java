package NativeClient;

import Model.ClientResponseModel;
import Model.RPCException;
import Model.RPCLog;
import Model.ServerRequestModel;
import RPCNet.Net;
import RPCNet.NetCore;
import Utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CustomWebSocketHandler extends SimpleChannelInboundHandler<Object> {
    private Client client;
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

    public CustomWebSocketHandler(Client client,WebSocketClientHandshaker handshaker){
        this.handshaker = handshaker;
        this.client = client;
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        System.out.println("handlerAdded");
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive");
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        client.onDisConnectEvent();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            System.out.println("channelRead0");
            Channel ch = ctx.channel();
            if (!handshaker.isHandshakeComplete()) {
                try {
                    handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                    client.onLog(RPCLog.LogCode.Runtime,"WebSocket Client connected!");
                    client.onConnectSuccess();
                    handshakeFuture.setSuccess();
                } catch (WebSocketHandshakeException e) {
                    handshakeFuture.setFailure(e);
                    throw new RPCException(RPCException.ErrorCode.Runtime,"WebSocket Client failed to connect");
                }
                return;
            }
            if (msg instanceof FullHttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;
                throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.getStatus()
                        + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
            }

            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof TextWebSocketFrame) {
                String data = ((TextWebSocketFrame) frame).text();
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                Net net = NetCore.get(client.getNetName());
                if(net == null){
                    throw new RPCException(RPCException.ErrorCode.Runtime,
                            String.format("未找到net", client.getNetName()));
                }
                JsonObject json_object = JsonParser.parseString(data).getAsJsonObject();

                if(json_object.get("Type").toString().equals("ER-1.0-ServerRequest")){
                    client.onLog(RPCLog.LogCode.Runtime,"[服-请求]:" + data);
                    //服务器模型的反序列化 实体
                    ServerRequestModel serverRequestModel = client.getConfig().getServerRequestModelDeserialize().Deserialize(data);
                    client.getEs().submit(()-> {
                        try {
                            net.getServerRequestReceive().ServerRequestReceive(serverRequestModel);
                        } catch (Exception e) {
                            client.onException(e);
                        }
                    });
                }
                else {
                    client.onLog(RPCLog.LogCode.Runtime,"[客-返回]:" + data);
                    ClientResponseModel clientResponseModel = client.getConfig().getClientResponseModelDeserialize().Deserialize(data);
                    client.getEs().submit(()->{
                        try {
                            net.getClientResponseReceive().ClientResponseReceive(clientResponseModel);
                        } catch (RPCException e) {
                            client.onException(e);
                        }
                    });
                }
            } else if (frame instanceof PongWebSocketFrame) {

            } else if (frame instanceof CloseWebSocketFrame) {
                client.disConnect();
            }
        }
        catch (Exception e){
            client.onException(e);
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
