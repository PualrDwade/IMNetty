package io.pualrdwade.github.server;

import generate.IMnettyChatProtocol.Message;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务器通道初始化器
 * 使用Spring容器管理依赖关系
 * 核心之策是控制数据协议编码解码
 *
 * @author PualrDwade
 */
@Component
public final class IMChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private IMChatMessageHandler messageHandler;// 消息IO处理器


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                // 使用google protobuf定制私有协议
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(Message.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(messageHandler);
    }
}