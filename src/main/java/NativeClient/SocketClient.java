    package NativeClient;

import Model.ClientRequestModel;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import Model.RPCException;
import Model.RPCLog;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.javatuples.Pair;

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
public class SocketClient {
    private Channel channel;
    private Bootstrap bootstrap;
    private Random random = new Random();
    private ClientConfig config;
    private String netName;
    private Pair<String,String> clientKey;

    public SocketClient(String netName,Pair<String,String> clientKey, ClientConfig config) {
        this.config = config;
        this.netName = netName;
        this.clientKey = clientKey;
    }
    public Pair<String, String> getClientKey() {
        return clientKey;
    }
    public void setClientKey(Pair<String, String> clientKey) {
        this.clientKey = clientKey;
    }
    public ClientConfig getConfig() {
        return config;
    }
    public void setConfig(ClientConfig config) {
        this.config = config;
    }
    public void start() {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        try {
            bootstrap = new Bootstrap();               //1
            bootstrap.group(group)                                //2
                    .channel(NioSocketChannel.class)            //3
                    .handler(new ChannelInitializer<SocketChannel>() {    //5
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new CustomDecoder(netName,clientKey,config));
                            ch.pipeline().addLast(new IdleStateHandler(0,0,5));
                            ch.pipeline().addLast(new CustomHeartbeatHandler(SocketClient.this));
                        }
                    });
            if(config.isNettyAdaptBuffer()){
                bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,new AdaptiveRecvByteBufAllocator());
            }
            else bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR,new FixedRecvByteBufAllocator(config.getBufferSize()));
            doConnect();
        }
        catch (Exception e){
            group.shutdownGracefully();
        }
    }

    public void doConnect() {
        if (channel != null && channel.isActive()) {
            return;
        }
        ChannelFuture future = bootstrap.connect(clientKey.getValue0(),Integer.parseInt(clientKey.getValue1()));
        future.addListener((ChannelFutureListener) futureListener -> {
            if (futureListener.isSuccess()) {
                channel = futureListener.channel();
                if(config.getConnectSuccess()!=null)config.getConnectSuccess().OnConnectSuccess();
                config.onLog(RPCLog.LogCode.Runtime,"Connect to server successfully!",this);
            }
            else {
                config.onException(RPCException.ErrorCode.Runtime,"Failed to connect to server, try connect after 10s",this);
                futureListener.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        doConnect();
                    }
                }, 10, TimeUnit.SECONDS);
            }
        });
    }
    public void disconnect() {
        channel.disconnect();
        channel.close();
    }
    public void send(ClientRequestModel request) {
        if(channel!=null && channel.isActive()){
            byte[] body =  config.getClientRequestModelSerialize().Serialize(request).getBytes(config.getCharset());
            int dataLength = body.length;  //读取消息的长度
            byte[] b = new byte[4];
            b[0] = (byte) (dataLength & 0xff);
            b[1] = (byte) (dataLength >> 8 & 0xff);
            b[2] = (byte) (dataLength >> 16 & 0xff);
            b[3] = (byte) (dataLength >> 24 & 0xff);
            byte[] pattern;
            pattern = new byte[]{0};
            byte[] future = new byte[27];
            ByteBuf out = new UnpooledHeapByteBuf(ByteBufAllocator.DEFAULT,32 + body.length,32 + body.length);
            out.writeBytes(b);
            out.writeBytes(pattern);
            out.writeBytes(future);
            out.writeBytes(body);  //消息体中包含我们要发送的数据
            channel.writeAndFlush(out);
        }
    }
}
