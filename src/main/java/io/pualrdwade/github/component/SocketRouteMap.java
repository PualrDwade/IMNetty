package io.pualrdwade.github.component;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 2019/3/28 后期使用redis实现分布式session共享

/**
 * socket路由映射表
 * 注册成为bean,使用spring容器进行管理
 * userId->channel映射,充当路由表
 *
 * @author PualrDwade
 */
@Component
public class SocketRouteMap extends ConcurrentHashMap<String, Channel> implements Map<String, Channel> {

}
