package NativeClient;

import java.nio.charset.StandardCharsets;
import java.util.List;

import Model.ClientRequestModel;
import Model.ClientResponseModel;
import Model.RPCException;
import Model.ServerRequestModel;
import RPCNet.NetConfig;
import RPCNet.NetCore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.javatuples.Pair;
import org.javatuples.Tuple;

public class CustomDecoder extends ByteToMessageDecoder {
    int headSize = 32;//头包长度
    int bodySize = 4;//数据大小长度
    int patternSize = 1;//消息类型长度
    int futureSize = 27;//后期看情况加
    ClientConfig config;
    Pair<String,String> clientKey;
    //下面这部分的byte用于接收数据
    private byte  pattern;
    private byte[] future = new byte[futureSize];
    private int dynamicAdjustBufferCount = -1;
    public CustomDecoder(Pair<String,String> clientKey, ClientConfig config){
        this.config = config;
        this.clientKey = clientKey;
    }
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws RPCException {
        in.resetReaderIndex();
        while(in.readerIndex() < in.writerIndex()){
            int count = in.writerIndex() - in.readerIndex();
            if(headSize < count){
                int body_length = in.getIntLE(in.readerIndex());
                byte pattern = in.getByte(in.readerIndex() + bodySize);
                byte[] future = new byte[futureSize];
                in.getBytes(in.readerIndex()+ bodySize + patternSize,future,0, futureSize);
                int length = body_length + headSize;
                if(length <= count){
                    NetConfig netConfig = NetCore.Get(clientKey);
                    if(netConfig == null){
                        throw new RPCException(RPCException.ErrorCode.RuntimeError,
                                "未找到%s-%s-NetConfig".formatted(clientKey.getValue0(),clientKey.getValue1()));
                    }
                    try{
                        String data = in.toString(in.readerIndex() + headSize,body_length,config.getCharset());
                        if(pattern == 0){
                            //Log.d(Tag.RemoteRepository,"[服-请求]:" + data);
                            ServerRequestModel serverRequestModel = Utils.Utils.gson.fromJson(data, ServerRequestModel.class);
                            netConfig.getServerRequestReceive().ServerRequestReceive(clientKey.getValue0(),clientKey.getValue1(),netConfig,serverRequestModel);

                        }
                        else {
                            //Log.d(Tag.RemoteRepository,"[客-返回]:" + data);
                            ClientResponseModel clientResponseModel = Utils.Utils.gson.fromJson(data, ClientResponseModel.class);
                            netConfig.getClientResponseReceive().ClientResponseReceive(clientKey.getValue0(),clientKey.getValue1(),netConfig,clientResponseModel);
                        }
                        in.readerIndex(in.readerIndex() + length);
                    }
                    catch(Exception e){
                        throw new RPCException(RPCException.ErrorCode.RuntimeError,"%s-%s:用户数据错误，已自动断开连接！"
                                .formatted(clientKey.getValue0() + ":" + clientKey.getValue1(),ctx.channel().remoteAddress()));
                    }
                }
                else {
                    if(in.readerIndex() != 0){
                        in.setBytes(0,in,in.readerIndex(),count);
                        in.resetReaderIndex();
                        in.writerIndex(count);
                    }
                    if(length > in.capacity()){
                        if(length < in.maxCapacity()){
                            in.capacity(length);
                            dynamicAdjustBufferCount = config.getDynamicAdjustBufferCount();
                            return;
                        }
                        else {
                            throw new RPCException(RPCException.ErrorCode.RuntimeError,"%s-%s:用户请求数据量太大，中止接收！"
                                    .formatted(clientKey.getValue0() + ":" + clientKey.getValue1(),ctx.channel().remoteAddress()));
                        }
                    }
                    return;
                }
            }
            else {
                if(in.readerIndex()!=0){
                    in.setBytes(0,in,in.readerIndex(),count);
                    in.resetReaderIndex();
                    in.writerIndex(count);
                }
                return;
            }
        }
        in.resetReaderIndex();
        in.resetWriterIndex();
        if(in.capacity() > config.getBufferSize() && dynamicAdjustBufferCount-- == 0){
            in.capacity(config.getBufferSize());
            dynamicAdjustBufferCount = 1;
        }
    }
}
