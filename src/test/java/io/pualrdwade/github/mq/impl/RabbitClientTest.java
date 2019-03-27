package io.pualrdwade.github.mq.impl;

import com.google.protobuf.ByteString;
import generate.IMnettyChatProtocol.Message;
import io.netty.util.CharsetUtil;
import io.pualrdwade.github.mq.MQClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class RabbitClientTest {

    private MQClient mqClient;

    @Before
    public void setUp() {
        mqClient = new RabbitClient();
    }

    @Test
    public void publishMessageForRight() throws Exception {
        Message requestMessage = Message.newBuilder()
                .setMessageType(Message.MessageType.CHAT)
                .setChatInfo(Message.ChatInfo.newBuilder()
                        .setChatType(Message.ChatInfo.ChatType.SINGLE)
                        .setContent(ByteString.copyFrom("你今天吃饭了吗~", CharsetUtil.UTF_8))
                        .setTimespace(new Date().getTime())
                        .setFromIp("544493924")
                        .addToIp("1023761678")
                        .setContentType(Message.ChatInfo.ContentType.TEXT)
                        .build()).build();
        mqClient.publishMessage(requestMessage);
    }
}