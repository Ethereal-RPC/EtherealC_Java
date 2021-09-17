package NativeClient.WebSocket;

import Core.Model.RPCLog;
import NativeClient.Abstract.Client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author xiongyongshun
 * @version 1.0
 * @email yongshun1228@gmail.com
 * @created 16/9/18 13:02
 */
public class CustomHeartbeatHandler extends ChannelInboundHandlerAdapter {
    private Client client;
    public CustomHeartbeatHandler(Client client){
        this.client = client;
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
    public void channelActive(ChannelHandlerContext ctx) {
        client.onLog(RPCLog.LogCode.Runtime,"---" + ctx.channel().remoteAddress() + " is state---");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        client.connect();
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