package io.pualrdwade.github.server;

import generate.IMnettyChatProtocol.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * 客户端连接器,作为消息生产者
 *
 * @author PualrDwade
 */
public class IMChatMessageHandler extends SimpleChannelInboundHandler<Message> {

    private BlockingQueue<Channel> chanelTaskQueue = null;

    private Map<String, Channel> routingMap = null;

    // TODO: 2019/3/26 使用依赖注入框架重构注入部分
    public IMChatMessageHandler(BlockingQueue<Channel> chanelTaskQueue, Map<String, Channel> routingMap) {
        this.chanelTaskQueue = chanelTaskQueue;
        this.routingMap = routingMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server:" + Thread.currentThread().getName());
        System.out.println("新客户端连接进来了,ip:" + ctx.channel().remoteAddress());
        // 用户连接注册到路由表中,表示用户已经连接
        routingMap.put(ctx.channel().remoteAddress().toString(), ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        // 绑定参数
        AttributeKey<Message> attributeKey = AttributeKey.valueOf("message");
        channelHandlerContext.channel().attr(attributeKey).set(message);//可能覆盖掉之前的
        // 添加进入生产者-消费者队列
        chanelTaskQueue.put(channelHandlerContext.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 从路由表中删除用户状态
        routingMap.remove(ctx.channel().remoteAddress().toString());
        System.out.println("[Server]用户:" + ctx.channel().remoteAddress() + "退出");
    }
}
