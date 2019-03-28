package io.pualrdwade.github.zookeeper;

import io.pualrdwade.github.core.ServiceRegistry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * 服务注册的实现类
 *
 * @author PualrDwade
 */
@Component
public class ZooKeeperRegistry implements ServiceRegistry {

    private static final int SESSION_TIMEOUT = 5000;

    private static final String REGISTRY_PATH = "/IMNetty-Registry";

    @Value("${zookeeper.host}")
    private String ZOOKEEPER_HOST;

    /**
     * 服务注册接口方法
     *
     * @param serviceName    服务名
     * @param serviceAddress 服务地址
     */
    @Override
    public void register(String serviceName, String serviceAddress) {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            ZooKeeper zk = new ZooKeeper(ZOOKEEPER_HOST, SESSION_TIMEOUT, watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected)
                    latch.countDown();
            });
            latch.await();
            String registryPath = REGISTRY_PATH;
            if (zk.exists(registryPath, false) == null) {
                zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //创建服务节点（持久节点）
            String servicePath = registryPath + "/" + serviceName;
            if (zk.exists(servicePath, false) == null) {
                zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //创建地址节点
            String addressPath = servicePath + "/address-";
            String addressNode = zk.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
