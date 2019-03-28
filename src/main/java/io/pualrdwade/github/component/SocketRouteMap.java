package io.pualrdwade.github.component;

import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

// TODO: 2019/3/28 后期使用redis实现分布式session共享
public class SocketRouteMap extends ConcurrentHashMap<String, Channel> {
}
