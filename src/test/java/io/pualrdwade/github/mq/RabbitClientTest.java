package io.pualrdwade.github.mq;

import com.google.protobuf.ByteString;
import generate.IMnettyChatProtocol.Message;
import io.netty.util.CharsetUtil;
import io.pualrdwade.github.Tester;
import io.pualrdwade.github.core.MQClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class RabbitClientTest extends Tester {

    @Autowired
    private MQClient mqClient;

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