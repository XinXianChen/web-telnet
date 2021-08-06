package xyz.chaobei.webtelnet.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import xyz.chaobei.webtelnet.server.handler.ServerChannelInitializer;
import xyz.chaobei.webtelnet.server.pojo.Node;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author xinxian
 * @create 2021-07-01 15:54
 **/
public class NettyServer {

    private final static Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private String host = "127.0.0.1";

    private int port = 12306;

    private Channel channel;

    public static ConcurrentMap<String, Node> nodeCache = new ConcurrentHashMap<>();

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("Server-boss", true));
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ServerChannelInitializer());

        if (StringUtils.isEmpty(host)) {
            channel = b.bind(port).sync().channel();
        } else {
            channel = b.bind(host, port).sync().channel();
        }

        logger.info("netty server listen at {}:{}", host, port);
        channel.closeFuture().sync();
    }


    public void stop() {
        if (channel != null) {
            channel.close();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

//    public static void main(String[] args) throws Exception {
//        NettyServer nettyServer = new NettyServer();
//        nettyServer.start();
//    }

}
