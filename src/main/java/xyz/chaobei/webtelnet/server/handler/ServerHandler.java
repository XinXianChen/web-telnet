package xyz.chaobei.webtelnet.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import xyz.chaobei.webtelnet.server.NettyServer;
import xyz.chaobei.webtelnet.server.pojo.Node;

/**
 * @author xinxian
 * @create 2021-07-01 16:30
 **/
public class ServerHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("channelRead0 ------------------ " + o);
        channelHandlerContext.writeAndFlush("server say hello");
        //ip:port
        final String address = o.toString();
        final String[] split = address.split(":");
        final Node node = new Node(Integer.parseInt(split[0]), split[1]);
        NettyServer.nodeCache.putIfAbsent(address,node);
    }
}
