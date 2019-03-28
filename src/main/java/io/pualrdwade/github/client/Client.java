package io.pualrdwade.github.client;

import com.google.protobuf.ByteString;
import generate.IMnettyChatProtocol.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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
import java.util.Scanner;

public class Client {

    private int port;

    Client(int port) {
        this.port = port;
    }

    private static final String SERVER_HOST = "localhost";

    private static final int SERVER_PORT = 8989;

    /**
     * 客户端启动方法
     *
     * @return
     * @throws InterruptedException
     */
    public ChannelFuture start() throws InterruptedException {
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
                        });
            }
        });
        return bootstrap.localAddress(port).connect(SERVER_HOST, SERVER_PORT).sync();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.out.println("非法启动参数");
            return;
        }
        int port = Integer.parseInt(args[0]);
        Client client = new Client(port);
        ChannelFuture channelFuture = client.start(); //初始化客户端
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                //启动界面线程
                new Thread(() -> {
                    System.out.println("...............欢迎进入IMNetty终端对点网络....................");
                    Channel channel = channelFuture.channel();
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("请输入发送目标的ip地址与端口:例如:[/127.0.0.1:8866]");
                    while (scanner.hasNext()) {
                        String toIp = scanner.nextLine();
                        System.out.println("请输入需要发送的内容");
                        String content = scanner.nextLine();
                        Message message = Message.newBuilder()
                                .setMessageType(Message.MessageType.CHAT)
                                .setChatInfo(Message.ChatInfo.newBuilder()
                                        .setChatType(Message.ChatInfo.ChatType.SINGLE)
                                        .setContent(ByteString.copyFrom(content, CharsetUtil.UTF_8))
                                        .setTimespace(new Date().getTime())
                                        .setFromIp(channel.localAddress().toString())
                                        .addToIp(toIp) // TODO: 2019/3/27 优化代码
                                        .setContentType(Message.ChatInfo.ContentType.TEXT)
                                        .build()).build();
                        channel.writeAndFlush(message);
                        System.out.println("请输入发送目标的ip地址与端口:例如:[/127.0.0.1:8866]");
                    }
                }).start();
            } else {
                future.cause();
            }
        });
    }
}
