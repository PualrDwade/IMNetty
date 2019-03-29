package io.pualrdwade.github.demo;

import io.pualrdwade.github.demo.client.RandomServerProvider;
import io.pualrdwade.github.demo.client.ServerProvider;
import org.junit.Test;

public class RandomServerProviderTest {

    private ServerProvider serverProvider = new RandomServerProvider();

    @Test
    public void getServer() {
        serverProvider.getServer();
    }
}