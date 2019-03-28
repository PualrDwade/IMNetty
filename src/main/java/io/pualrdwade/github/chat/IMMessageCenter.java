package io.pualrdwade.github.chat;

import generate.IMnettyChatProtocol.Message;
import io.netty.channel.Channel;
import io.pualrdwade.github.core.MQClient;
import io.pualrdwade.github.mq.RabbitClient;

import java.util.Map;


/**
 * 消息订阅中心,从消息队列中拿到订阅的消息,发送给用户
 * IMNetty即时通讯的核心组件
 *
 * @author PualrDwade
 * @create 2019-2-3
 */
public class IMMessageCenter extends Thread {

    private volatile boolean work = false;

    //userId->channel映射,充当路由表
    // 消息中心用来记录连接到此服务器的用户
    private Map<String, Channel> routingMap = null;

    private MQClient mqClient = null;

    public IMMessageCenter(Map<String, Channel> map) {
        this.routingMap = map;
        this.mqClient = new RabbitClient();
    }

    @Override
    public synchronized void start() {
        this.work = true;
        super.start();
        System.out.println("MessageDispatcher started!");
    }

    @Override
    public void run() {
        try {
            mqClient.subscribeMessage(message -> {
                System.out.println("消息:\n" + message + message.getChatInfo().getContent().toStringUtf8());
                if (message.getChatInfo() == null) {
                    return;
                }
                if (message.getChatInfo().getChatType().equals(Message.ChatInfo.ChatType.SINGLE)) {
                    String fromIp = message.getChatInfo().getFromIp();
                    String toIp = message.getChatInfo().getToIp(0);
                    //判断toUser是否在与user在同一个服务器,在的话就处理,不在就不管了
                    if (this.routingMap.containsKey(toIp)) {
                        Channel toUserChannel = this.routingMap.get(toIp);
                        toUserChannel.writeAndFlush(message);
                        System.out.println("Server:用户[" + fromIp + "]向用户[" + toIp + "]发送消息:" + message.getChatInfo().getContent().toStringUtf8());
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
