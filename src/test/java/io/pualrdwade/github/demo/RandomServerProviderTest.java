package io.pualrdwade.github.demo;

import io.pualrdwade.github.demo.client.RandomServerProvider;
import io.pualrdwade.github.demo.client.ServerProvider;
import org.junit.Assert;
import org.junit.Test;

public class RandomServerProviderTest {

    private ServerProvider serverProvider = new RandomServerProvider();

    @Test
    public void getServer() {
        try {
            serverProvider.getServer();
        } catch (RuntimeException e) {
            Assert.assertEquals("没有可用服务器!", e.getMessage());
        }
    }
}