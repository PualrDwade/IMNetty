package io.pualrdwade.github.zookeeper;

import io.pualrdwade.github.core.ServiceRegistry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 服务注册的实现类
 *
 * @author PualrDwade
 */
public class ZooKeeperRegistry implements ServiceRegistry {

    private static Logger logger = LoggerFactory.getLogger(ZooKeeperRegistry.class);
    private static CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;
    private static final int SESSION_TIMEOUT = 5000;
    private static final String ZK_HOST = "120.79.206.32";
    private static final String REGISTRY_PATH = "/IMNetty-Registry";

    public ZooKeeperRegistry() {
        try {
            zk = new ZooKeeper(ZK_HOST, SESSION_TIMEOUT, watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected)
                    latch.countDown();
            });
            latch.await();
            logger.debug("connected to zookeeper");
        } catch (Exception e) {
            logger.error("create zookeeper client failure", e);
        }
    }

    /**
     * 服务注册接口方法
     *
     * @param serviceName    服务名
     * @param serviceAddress 服务地址
     */
    @Override
    public void register(String serviceName, String serviceAddress) {
        try {
            String registryPath = REGISTRY_PATH;
            if (zk.exists(registryPath, false) == null) {
                zk.create(registryPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.debug("create registry node:{}", registryPath);
            }
            //创建服务节点（持久节点）
            String servicePath = registryPath + "/" + serviceName;
            if (zk.exists(servicePath, false) == null) {
                zk.create(servicePath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.debug("create service node:{}", servicePath);
            }
            //创建地址节点
            String addressPath = servicePath + "/address-";
            String addressNode = zk.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.debug("create address node:{} => {}", addressNode, serviceAddress);
        } catch (Exception e) {
            logger.error("create node failure", e);
        }
    }
}
