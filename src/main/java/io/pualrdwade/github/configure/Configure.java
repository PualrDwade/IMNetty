package io.pualrdwade.github.configure;

import io.pualrdwade.github.component.ChannelTaskQueue;
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

}
