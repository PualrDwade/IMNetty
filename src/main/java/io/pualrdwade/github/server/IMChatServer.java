package io.pualrdwade.github.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.pualrdwade.github.chat.IMMessageCenter;

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

    //todo 调整阻塞队列的并发量以适应CPU
    private static final int CONCURRENCY = 10000;

    public static void main(String[] args) throws InterruptedException {
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
