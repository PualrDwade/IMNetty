# IMNetty
## 介绍
IMNetty是一款基于Netty的高性能IM即时通讯组件,支持点对点聊天与群聊,支持定制拓展业务,支持分布式多机部署,支持用户添加自定义通讯协议,广泛适用于项目的基础通讯模块

## 技术体系

使用Netty作为网络通信框架,使用Redis记录登陆状态与路由信息,使用RabbitMQ解耦,实现分布式聊天队列,使用Zookeeper实现服务注册发现,在网关层实现负载均衡与服务接入

## 架构

基于Netty的事件驱动非阻塞模型改造的Preactor模型,执行管道流风格,让Netty的IO线程专注于进行网络,让CPU解放出来执行业务逻辑

![](https://i.loli.net/2019/03/27/5c9b2179c3882.png)
