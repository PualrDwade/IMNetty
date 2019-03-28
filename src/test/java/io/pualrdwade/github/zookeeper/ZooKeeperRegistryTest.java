package io.pualrdwade.github.zookeeper;

import io.pualrdwade.github.core.ServiceRegistry;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ZooKeeperRegistryTest {

    ServiceRegistry serviceRegistry;

    @Before
    public void setUp() {
        BasicConfigurator.configure();
        serviceRegistry = new ZooKeeperRegistry();
    }

    @Test
    public void registerForRight() throws UnknownHostException {
        serviceRegistry.register("IMNetty", InetAddress.getLocalHost().getHostAddress());
    }
}