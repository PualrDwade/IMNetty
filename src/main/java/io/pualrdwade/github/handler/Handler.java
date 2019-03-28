package io.pualrdwade.github.handler;

/**
 * @author PualrDwade
 * 使用命令模式+回调,对事件进行处理
 */
@FunctionalInterface
public interface Handler<P, R> {

    R handle(P param);

}
