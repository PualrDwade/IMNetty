package io.pualrdwade.github.configure;

import io.pualrdwade.github.component.ChannelTaskQueue;
import io.pualrdwade.github.component.SocketRouteMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configure {

    /**
     * 任务调度消息队列bean
     *
     * @return
     */
    @Bean
    public ChannelTaskQueue channelTaskQueue() {
        return new ChannelTaskQueue(10000);
    }


    /**
     * socket路由映射表
     * 注册成为bean,使用spring容器进行管理
     * userId->channel映射,充当路由表
     *
     * @return
     */
    @Bean
    public SocketRouteMap socketRouteMap() {
        return new SocketRouteMap();
    }

}
