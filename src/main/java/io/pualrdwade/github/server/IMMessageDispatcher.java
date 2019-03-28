package io.pualrdwade.github.server;

import generate.IMnettyChatProtocol;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.pualrdwade.github.component.ChannelTaskQueue;
import io.pualrdwade.github.component.SocketRouteMap;
import io.pualrdwade.github.core.MQClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消息中心,进行消息的接受与分发处理
 * 观察者模式实现,同时作为内部队列的消费者
 * 与NIO模块使用阻塞队列的方法解开耦合,让IO线程得以高效运转
 * 能够有效压榨CPU,提高系统吞吐量
 *
 * @author PualrDwade
 * @crate 2019-2-2
 */
@Component
public final class IMMessageDispatcher implements Runnable {

    @Autowired
    private ChannelTaskQueue channelTaskQueue;

    @Autowired
    private MQClient mqClient; //注入消息队列操作类

    @Autowired
    private SocketRouteMap socketRouteMap;

    //use the system kernel predict the initializee the thread number
    private final int DEFAULT_THREAD_NUMBER = Runtime.getRuntime().availableProcessors();

    private volatile boolean work = false;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private static Logger logger = Logger.getLogger(IMMessageDispatcher.class);

    /**
     * 为消息中心注册连接
     *
     * @param channel
     * @throws InterruptedException
     */
    public void putChannel(Channel channel) throws InterruptedException {
        this.channelTaskQueue.put(channel);
    }

    @Override
    public void run() {
        try {
            while (work) {
                // take a channel from the channelBlockingQueue in additon to doDispatch
                // todo 重构使用消息队列,实现分布式
                Channel channel = this.channelTaskQueue.take();
                // begin doDispatch the socket todo 拆分业务模块
                doDispatch(channel);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将channel注册进入map
     *
     * @param channel
     */
    public void registerChannel(String userId, Channel channel) {
        if (this.socketRouteMap.containsKey(userId)) {
            logger.info("[Server]:此ip用户连接已存在!");
            return;
        }
        if (channel == null) {
            logger.info("[Server]:channel 参数错误,不能为null");
            return;
        }
        this.socketRouteMap.put(userId, channel);
    }


    //启动消息中心
    public void start() {
        this.work = true;
        for (int i = 0; i < DEFAULT_THREAD_NUMBER; ++i) {
            this.executorService.execute(this);
        }
    }

    // 关闭消息中心
    public void shutdownGracefully() {
        this.work = false;
    }


    /**
     * 核心方法,针对消息实体进行派发
     *
     * @param channel
     */
    private void doDispatch(Channel channel) {
        AttributeKey<IMnettyChatProtocol.Message> attributeKey = AttributeKey.valueOf("message");
        if (!channel.hasAttr(attributeKey)) {
            logger.info("[Server]:channel 没有 message!");
            return;
        }
        IMnettyChatProtocol.Message message = channel.attr(attributeKey).get();
        // TODO: 2019/3/26 重构聊天模块,提取出单独的业务模块
        switch (message.getMessageType()) {
            case CHAT: {
                try {
                    this.mqClient.publishMessage(message);
                } catch (Exception e) {
                    logger.info("[Server]:消息:" + message + "推送失败!");
                }
            }
            case PING: {
            }
            case UNRECOGNIZED: {

            }
        }
    }
}
