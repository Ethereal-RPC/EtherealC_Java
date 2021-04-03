package NativeClient;

import Model.ClientRequestModel;
import java.net.SocketException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CustomEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] body = (Utils.Utils.gson.toJson(msg)).getBytes("UTF-8");
        int dataLength = body.length;  //读取消息的长度
        byte[] b = new byte[4];
        b[0] = (byte) (dataLength & 0xff);
        b[1] = (byte) (dataLength >> 8 & 0xff);
        b[2] = (byte) (dataLength >> 16 & 0xff);
        b[3] = (byte) (dataLength >> 24 & 0xff);
        byte[] pattern;
        if(msg instanceof ClientRequestModel){
            if(((ClientRequestModel) msg).getId() != null){
                pattern = new byte[]{0};
            }
            else pattern = new byte[]{1};
        }
        else throw new SocketException("发送类[ClientRequestModel]异常");
        byte[] future = new byte[27];
        out.writeBytes(b);
        out.writeBytes(pattern);
        out.writeBytes(future);
        out.writeBytes(body);  //消息体中包含我们要发送的数据
    }
}