package io.pualrdwade.github.core;

/**
 * 使用命令模式+回调实现大部分异步时间
 * 异步对事件进行处理
 *
 * @author PualrDwade
 * @create at 2019-3-2
 */
@FunctionalInterface
public interface Handler<E> {

    void handle(E event);

}
