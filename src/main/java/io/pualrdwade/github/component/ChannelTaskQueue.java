package io.pualrdwade.github.component;

import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 工作线程处理的任务队列
 * 注册成为Bean,让Spring容器进行管理
 *
 * @author PualrDwade
 */

@Component
public class ChannelTaskQueue {

    // 任务队列的大小,用于进行拥塞并发量控制
    @Value("${imserver.queuesize}")
    private int QUEUE_SIZE;

    private BlockingQueue<Channel> blockingQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);

    /**
     * 从队列中获取一个任务进行调度
     *
     * @return
     * @throws InterruptedException
     */
    public Channel take() throws InterruptedException {
        return this.blockingQueue.take();
    }

    /**
     * 往队列中防止一个待处理任务
     *
     * @throws InterruptedException
     */
    public void put(Channel channel) throws InterruptedException {
        this.blockingQueue.put(channel);
    }

}
