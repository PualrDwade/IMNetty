package io.pualrdwade.github.chat;

import generate.IMnettyChatProtocol.Message;
import io.netty.channel.Channel;
import io.pualrdwade.github.component.SocketRouteMap;
import io.pualrdwade.github.core.MQClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 消息订阅中心,从消息队列中拿到订阅的消息,发送给用户
 * IMNetty即时通讯的核心组件
 * 使用Spring容器管理其生命周期
 *
 * @author PualrDwade
 * @create 2019-2-3
 */
@Component
public class IMMessageCenter extends Thread {

    private volatile boolean work = false;

    private static Logger logger = Logger.getLogger(IMMessageCenter.class);

    @Autowired
    private SocketRouteMap socketRouteMap;

    @Autowired
    private MQClient mqClient;

    @Override
    public synchronized void start() {
        this.work = true;
        super.start();
    }

    @Override
    public void run() {
        try {
            mqClient.subscribeMessage(message -> {
                logger.info("[Server]:订阅到消息:" + message);
                if (message.getChatInfo() == null) {
                    return;
                }
                if (message.getChatInfo().getChatType().equals(Message.ChatInfo.ChatType.SINGLE)) {
                    String fromIp = message.getChatInfo().getFromIp();
                    String toIp = message.getChatInfo().getToIp(0);
                    //判断toUser是否在与user在同一个服务器,在的话就处理,不在就不管了
                    if (this.socketRouteMap.containsKey(toIp)) {
                        Channel toUserChannel = this.socketRouteMap.get(toIp);
                        toUserChannel.writeAndFlush(message);
                        logger.info("[Server]:用户[" + fromIp + "]向用户[" + toIp + "]发送消息:" + message.getChatInfo().getContent().toStringUtf8());
                    }
                } else if (message.getChatInfo().getChatType().equals(Message.ChatInfo.ChatType.GROUP)) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdownGracefully() {
        this.work = false;
    }

}
