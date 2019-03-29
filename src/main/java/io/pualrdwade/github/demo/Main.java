package io.pualrdwade.github.demo;

import com.google.protobuf.ByteString;
import generate.IMnettyChatProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.CharsetUtil;
import io.pualrdwade.github.demo.client.Client;
import io.pualrdwade.github.demo.client.RandomServerProvider;
import io.pualrdwade.github.demo.client.ServerProvider;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

/**
 * 客户端演示
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.out.println("非法启动参数");
            return;
        }
        int port = Integer.parseInt(args[0]);
        Client client = new Client(port);
        ServerProvider serverProvider = new RandomServerProvider(); //随机选择服务器
        client.getContext().setServerProvider(serverProvider); //注入到客户端上下文环境中
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
                        IMnettyChatProtocol.Message message = IMnettyChatProtocol.Message.newBuilder()
                                .setMessageType(IMnettyChatProtocol.Message.MessageType.CHAT)
                                .setChatInfo(IMnettyChatProtocol.Message.ChatInfo.newBuilder()
                                        .setChatType(IMnettyChatProtocol.Message.ChatInfo.ChatType.SINGLE)
                                        .setContent(ByteString.copyFrom(content, CharsetUtil.UTF_8))
                                        .setTimespace(new Date().getTime())
                                        .setFromIp(channel.localAddress().toString())
                                        .addToIp(toIp) // TODO: 2019/3/27 优化代码
                                        .setContentType(IMnettyChatProtocol.Message.ChatInfo.ContentType.TEXT)
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
