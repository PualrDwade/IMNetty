package io.pualrdwade.github.server;

import generate.IMnettyChatProtocol.Message;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.pualrdwade.github.component.ChannelTaskQueue;
import io.pualrdwade.github.component.SocketRouteMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 客户端连接处理器,作为消息生产者
 * IO线程的主要负责类,唯一的职责就是进行IO操作,监听事件
 *
 * @author PualrDwade
 */

@Sharable
@Component
public class IMChatMessageHandler extends SimpleChannelInboundHandler<Message> {

    @Autowired
    private ChannelTaskQueue channelTaskQueue;

    @Autowired
    private SocketRouteMap socketRouteMap;

    private static Logger logger = Logger.getLogger(IMChatMessageHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("[Server]:新客户端连接进来了,ip:" + ctx.channel().remoteAddress());
        // 用户连接注册到路由表中,表示用户已经连接
        this.socketRouteMap.put(ctx.channel().remoteAddress().toString(), ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        // 绑定参数
        AttributeKey<Message> attributeKey = AttributeKey.valueOf("message");
        channelHandlerContext.channel().attr(attributeKey).set(message);//可能覆盖掉之前的
        // 添加进入生产者-消费者队列
        this.channelTaskQueue.put(channelHandlerContext.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 从路由表中删除用户状态
        this.socketRouteMap.remove(ctx.channel().remoteAddress().toString());
        logger.info("[Server]:用户" + ctx.channel().remoteAddress() + "退出");
    }
}
