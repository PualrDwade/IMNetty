package io.pualrdwade.github.core;

import generate.IMnettyChatProtocol;
import io.pualrdwade.github.core.Handler;

public interface MQClient {

    /**
     * 发布消息到消息队列,作为生产者
     *
     * @param requestMessage
     */
    void publishMessage(generate.IMnettyChatProtocol.Message message) throws Exception;


    /**
     * 进行消息的订阅操作,此操作会一直持续
     *
     * @throws Exception
     */
    void subscribeMessage(Handler<IMnettyChatProtocol.Message> handler) throws Exception;
}
