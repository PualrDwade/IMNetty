package io.pualrdwade.github.server;

import generate.IMnettyChatProtocol.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * 服务器通道初始化器
 *
 * @author PualrDwade
 */
public final class IMChannelInitializer extends ChannelInitializer<SocketChannel> {

    private BlockingQueue<Channel> chanelTaskQueue = null;

    private Map<String, Channel> routingMap = null;

    // TODO: 2019/3/26 使用依赖注入框架重构注入部分
    public IMChannelInitializer(BlockingQueue<Channel> chanelTaskQueue, Map<String, Channel> routingMap) {
        this.chanelTaskQueue = chanelTaskQueue;
        this.routingMap = routingMap;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                // 使用google protobuf定制私有协议
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(Message.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(new IMChatMessageHandler(this.chanelTaskQueue, this.routingMap));
    }
}