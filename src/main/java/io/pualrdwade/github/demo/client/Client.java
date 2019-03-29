package io.pualrdwade.github.demo.client;

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

import java.util.Map;

public class Client {

    private ClientContext context;

    public ClientContext getContext() {
        return context;
    }

    private int port;

    public Client(int port) {
        context = new ClientContext();
        this.port = port;
    }

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
        // 得到服务器
        Map<String, String> map = getContext().getServerProvider().getServer();
        return bootstrap.localAddress(port).connect(map.get("host"), Integer.parseInt(map.get("port"))).sync();
    }
}
