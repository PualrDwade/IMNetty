package io.pualrdwade.github.core;

/**
 * 服务注册发现接口
 *
 * @author PualrDwade
 */
public interface ServiceRegistry {

    /**
     * 服务注册接口方法
     *
     * @param serviceName    服务名
     * @param serviceAddress 服务地址
     */
    public void register(String serviceName, String serviceAddress);

}
