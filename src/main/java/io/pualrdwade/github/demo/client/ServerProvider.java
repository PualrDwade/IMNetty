package io.pualrdwade.github.demo.client;


import java.util.Map;

/**
 * 从配置中心通过不同的算法来进行负载均衡
 * 使用了策略模式
 *
 * @author
 */
public interface ServerProvider {

    Map<String, String> getServer();

}
