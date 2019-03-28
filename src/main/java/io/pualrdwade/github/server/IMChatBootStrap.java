package io.pualrdwade.github.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.pualrdwade.github.chat.IMMessageCenter;
import io.pualrdwade.github.core.ServiceRegistry;
import io.pualrdwade.github.zookeeper.ZooKeeperRegistry;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * 服务器启动类,注册为Bean让Spring容器进行管理
 *
 * @author PualrDwade
 * @version 2.0
 */

@Component
public final class IMChatBootStrap {

    @Value("${imserver.port}")
    private static int SERVER_PORT;

    @Autowired
    private IMMessageDispatcher messageDispatcher; //消息中央派发器

    @Autowired
    private IMMessageCenter messageCenter; //聊天消息中心

    @Autowired
    private IMChannelInitializer channelInitializer; //通道初始化器

    private static Logger logger = LoggerFactory.getLogger(ZooKeeperRegistry.class);

    @PostConstruct
    public void start() throws InterruptedException, UnknownHostException {
        BasicConfigurator.configure();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup workers = new NioEventLoopGroup();
        try {
            ServiceRegistry serviceRegistry = new ZooKeeperRegistry();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, workers);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(channelInitializer);
            messageDispatcher.start();
            messageCenter.start();
            serverBootstrap.bind(SERVER_PORT).addListener(future -> {
                if (future.isSuccess()) {
                    // 成功启动服务器之后注册到服务中心
                    serviceRegistry.register("IMNetty", InetAddress.getLocalHost().getHostAddress() + ":" + SERVER_PORT);
                }
            }).sync().channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
            messageDispatcher.shutdownGracefully();
            messageCenter.shutdownGracefully();
        }
    }
}
