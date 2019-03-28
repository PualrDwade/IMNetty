package io.pualrdwade.github.zookeeper;

import io.pualrdwade.github.Tester;
import io.pualrdwade.github.core.ServiceRegistry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ZooKeeperRegistryTest extends Tester {

    @Autowired
    ServiceRegistry serviceRegistry;

    @Test
    public void registerForRight() throws UnknownHostException {
        serviceRegistry.register("IMNetty", InetAddress.getLocalHost().getHostAddress());
    }
}