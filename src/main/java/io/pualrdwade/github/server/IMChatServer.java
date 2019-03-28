package io.pualrdwade.github.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.pualrdwade.github.chat.IMMessageCenter;
import io.pualrdwade.github.core.ServiceRegistry;
import io.pualrdwade.github.zookeeper.ZooKeeperRegistry;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 聊天服务,单机版本实现
 *
 * @author PualrDwade
 * @version 1.0
 */
public final class IMChatServer {

    private static final String ZOOKEEPER_HOST = "120.79.206.32";

    private static final int PORT = 8989;

    //todo 调整阻塞队列的并发量以适应CPU
    private static final int CONCURRENCY = 10000;

    private static Logger logger = LoggerFactory.getLogger(ZooKeeperRegistry.class);

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        BasicConfigurator.configure();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup workers = new NioEventLoopGroup();
        // 使用消息队列进行解耦,一方面可以解放IO线程与业务线程,还可以提高吞吐量
        BlockingQueue<Channel> chanelTaskQueue = new ArrayBlockingQueue<>(CONCURRENCY);
        IMMessageDispatcher messageDispatcher = new IMMessageDispatcher(chanelTaskQueue);
        //userId->channel映射,充当路由表
        // 消息中心用来记录连接到此服务器的用户
        Map<String, Channel> routingMap = new ConcurrentHashMap<>();
        IMMessageCenter messageCenter = new IMMessageCenter(routingMap);
        try {
            //启动之前首先注册到服务注册中心
            ServiceRegistry serviceRegistry = new ZooKeeperRegistry();
            serviceRegistry.register("IMNetty", InetAddress.getLocalHost().getHostAddress() + ":" + PORT);
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, workers);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new IMChannelInitializer(chanelTaskQueue, routingMap));
            messageDispatcher.start();
            messageCenter.start();
            serverBootstrap.bind(8989).sync().channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
            messageDispatcher.shutdownGracefully();
            messageCenter.shutdownGracefully();
        }
    }
}
