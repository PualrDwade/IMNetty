# IMNetty

![](https://img.shields.io/badge/语言-java-red.svg)   ![](https://img.shields.io/badge/技术-zookeeper&&rabbitMQ&&redis-green.svg)   ![](https://img.shields.io/badge/依赖-netty&&protobuf-orange.svg)

## 介绍
`IMNetty`是一款基于`Netty`的高性能IM即时通讯组件,支持点对点聊天与群聊,支持定制拓展业务,支持分布式多机部署,支持用户添加自定义通讯协议,广泛适用于项目的基础通讯模块

## 技术体系

使用`Netty`作为网络通信框架,使用`Redis`记录登陆状态与路由信息,使用RabbitMQ解耦,实现分布式聊天队列,使用Zookeeper实现服务注册发现,在网关层实现负载均衡与服务接入

## 架构

基于Netty的事件驱动非阻塞模型改造的Preactor模型,执行管道流风格,让Netty的IO线程专注于进行网络,让CPU解放出来执行业务逻辑

![](https://i.loli.net/2019/03/27/5c9b2179c3882.png)

## 部署
启动类`IMChatServer`,在启动成功之后便会将自己注册到zookeeper服务注册中心之中,通过zookeeper实现服务的注册与发现,每个服务实体共享消息队列集群,从而完成分布式的即时通讯基础组件的架构部署

如图:通过`zkui`可视化监控工具可以看到我们的分布式部署结果

![](https://i.loli.net/2019/03/28/5c9c5f3994398.png)



## 待完善

- [x] 整合`SpringBoot`
- [ ] - [ ] 统一权限验证网关
- [ ] 优化代码结构提取公共配置
- [ ] 客户端登陆状态的会话状态`Redis`集群

