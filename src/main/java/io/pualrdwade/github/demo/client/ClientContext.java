package io.pualrdwade.github.demo.client;

/**
 * 客户端上下文环境类
 *
 * @author PualrDwade
 */
public class ClientContext {

    private ServerProvider serverProvider;

    public ServerProvider getServerProvider() {
        return serverProvider;
    }

    public void setServerProvider(ServerProvider serverProvider) {
        this.serverProvider = serverProvider;
    }
}
