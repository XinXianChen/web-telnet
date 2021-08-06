package xyz.chaobei.webtelnet.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.chaobei.webtelnet.server.NettyServer;

/**
 * @author xinxian
 * @create 2021-07-01 15:53
 **/
//@Configuration
public class NettyServerConfiguration {

//    @Bean(initMethod = "start", destroyMethod = "stop")
    public NettyServer nettyServer() {
        NettyServer nettyServer = new NettyServer();
        return nettyServer;
    }
}
