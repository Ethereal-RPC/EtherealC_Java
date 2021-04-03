package NativeClient;

import Model.ClientRequestModel;
import Model.ClientResponseModel;
import java.lang.reflect.Method;

import RPCNet.NetConfig;
import RPCNet.NetCore;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author xiongyongshun
 * @version 1.0
 * @email yongshun1228@gmail.com
 * @created 16/9/18 13:02
 */
public class CustomHeartbeatHandler extends ChannelHandlerAdapter {
    private int heartbeatCount = 0;
    private SocketClient socketClient;
    public CustomHeartbeatHandler(SocketClient socketClient){
        this.socketClient = socketClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NetConfig netConfig = NetCore.Get(socketClient.getClientKey());
        if(msg instanceof ClientResponseModel){
            netConfig.getClientResponseReceive().ClientResponseReceive(socketClient.getClientKey().getValue0(),socketClient.getClientKey().getValue1(),netConfig,(ClientResponseModel)msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("---" + ctx.channel().remoteAddress() + " is state---");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        socketClient.doConnect();
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        System.out.println("---READER_IDLE---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        System.err.println("---WRITER_IDLE---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {

    }
}