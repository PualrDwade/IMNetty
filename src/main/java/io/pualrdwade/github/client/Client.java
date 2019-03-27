package io.pualrdwade.github.client;

import com.google.protobuf.ByteString;
import generate.IMnettyChatProtocol.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.Date;

public class Client implements Runnable {


    private int port;

    public Client(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // 设置端口
        if (args != null && args.length != 0) {
            String userIp = args[0];
            new Thread(new Client(Integer.parseInt(userIp))).start();
        }
    }

    public void run() {
        EventLoopGroup client = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(client).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        // 使用自定义协议
                        .addLast(new ProtobufVarint32FrameDecoder())
                        .addLast(new ProtobufDecoder(Message.getDefaultInstance()))
                        .addLast(new ProtobufVarint32LengthFieldPrepender())
                        .addLast(new ProtobufEncoder())
                        .addLast(new SimpleChannelInboundHandler<Message>() {

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
                                System.out.println("[Client]收到来自用户:" + message.getChatInfo().getFromIp()
                                        + "的消息:" + message.getChatInfo().getContent().toStringUtf8());
                            }

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                super.channelActive(ctx);
                                String content = "你好!我是新来的!";
                                System.out.println("[Client]:和服务端建立连接,发送消息给客户端:" + content);
                                Message message = Message.newBuilder()
                                        .setMessageType(Message.MessageType.CHAT)
                                        .setChatInfo(Message.ChatInfo.newBuilder()
                                                .setChatType(Message.ChatInfo.ChatType.SINGLE)
                                                .setContent(ByteString.copyFrom(content, CharsetUtil.UTF_8))
                                                .setTimespace(new Date().getTime())
                                                .setFromIp(ctx.channel().localAddress().toString())
                                                .addToIp(ctx.channel().localAddress().toString()) //给自己发消息
                                                .setContentType(Message.ChatInfo.ContentType.TEXT)
                                                .build()).build();
                                ctx.channel().writeAndFlush(message);
                            }
                        });
            }
        });
        try {
            bootstrap.localAddress(port).connect("localhost", 8989).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
