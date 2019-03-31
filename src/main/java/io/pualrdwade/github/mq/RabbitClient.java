package io.pualrdwade.github.mq;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import generate.IMnettyChatProtocol.Message;
import io.pualrdwade.github.core.Handler;
import io.pualrdwade.github.core.MQClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

/**
 * 注册为bean,使用Spring容器管理
 *
 * @author PualrDwade
 */
@Component
public class RabbitClient implements MQClient {

    @Value("${rabbitmq.queuename}")
    private String EXCHANGE_NAME;

    @Value("${rabbitmq.host}")
    private String RABBIT_HOST;

    @Value("${rabbitmq.port}")
    private Integer RABBIT_PORT;

    @Autowired
    private ExecutorService workerThreadPool;

    private static Logger logger = Logger.getLogger(RabbitClient.class);

    /**
     * 发布消息到消息队列,作为生产者
     * 基于发布-订阅模型,一个消息可以广播到多个订阅者
     *
     * @param requestMessage
     */
    @Override
    public void publishMessage(Message requestMessage) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        channel.basicPublish(EXCHANGE_NAME, "", null, requestMessage.toByteArray());
        logger.info("[Server]:发布了聊天消息到消息队列:" + EXCHANGE_NAME);
        channel.close();
        connection.close();
    }

    /**
     * 进行消息的订阅操作,此操作会一直持续
     *
     * @param handler 回调函数
     * @throws Exception
     */
    @Override
    public void subscribeMessage(Handler<Message> handler) throws Exception {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();// 产生一个不持久化、独占的、自动删除的、随机命名的队列
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 使用工作线程池执行
                workerThreadPool.execute(() -> {
                    //解析消息队列数据
                    try {
                        Message message = Message.parseFrom(body);
                        handler.handle(message);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                });
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }

    // 得到连接
    private Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RABBIT_HOST);
        connectionFactory.setPort(RABBIT_PORT);
        return connectionFactory.newConnection();
    }
}