package xyz.chaobei.webtelnet.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import xyz.chaobei.webtelnet.server.handler.ClientHandler;

import java.io.IOException;
import java.net.Socket;

/**
 * @author xinxian
 * @create 2021-07-01 16:55
 **/
public class NettyClient {

    public static final String HOST = "127.0.0.1";

    public static final int PORT = 12306;

    public static void main(String[] args) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new StringDecoder());
                            p.addLast("encoder", new StringEncoder());
                            p.addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = b.connect(HOST, PORT).sync();
            future.channel().writeAndFlush("xinxian");
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }


        Socket socket = new Socket("127.0.0,1",12306);



    }

}
