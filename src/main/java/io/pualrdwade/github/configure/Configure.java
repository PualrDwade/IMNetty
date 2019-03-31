package io.pualrdwade.github.configure;

import io.pualrdwade.github.component.ChannelTaskQueue;
import io.pualrdwade.github.component.SocketRouteMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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


    /**
     * 工作主线程Bean
     * 托管Spring管理,提供给派发器与消息中心使用
     * 统一作为工作线程池
     *
     * @return
     */
    @Bean
    public ExecutorService workerThreadPool() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });
    }
}
