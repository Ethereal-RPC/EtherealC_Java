    package NativeClient;

import Model.ClientRequestModel;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import NativeClient.Interface.IConnectSuccess;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
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
    private Pair<String,String> clientKey;

    public SocketClient(Pair<String,String> clientKey, ClientConfig config) {
        this.config = config;
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
                    .option(ChannelOption.RCVBUF_ALLOCATOR,new FixedRecvByteBufAllocator(config.getBufferSize()))
                    .channel(NioSocketChannel.class)            //3
                    .handler(new ChannelInitializer<SocketChannel>() {    //5
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(new CustomDecoder());
                            ch.pipeline().addLast(new IdleStateHandler(0,0,5));
                            ch.pipeline().addLast(new CustomHeartbeatHandler(SocketClient.this));
                            ch.pipeline().addLast(new CustomEncoder());
                        }
                    });
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
                System.out.println("Connect to server successfully!");
            }
            else {
                System.out.println("Failed to connect to server, try connect after 10s");
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
            channel.writeAndFlush(request);
        }
    }
}
