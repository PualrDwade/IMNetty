package io.pualrdwade.github.demo.client;

import org.apache.zookeeper.ZooKeeper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;

/**
 * 随机算法
 */
public class RandomServerProvider implements ServerProvider {

    private static final String ZK_HOST = "120.79.206.32";

    private static final String REGISTRY_PATH = "/IMNetty-Registry/IMNetty";

    @Override
    public Map<String, String> getServer() {
        CountDownLatch latch = new CountDownLatch(1);
        Map<String, String> map = new HashMap<>();
        try {
            ZooKeeper zooKeeper = new ZooKeeper(ZK_HOST, 5000, event -> {
                if (event.getState() == SyncConnected) {
                    latch.countDown();
                }
            });
            latch.await();
            List<String> childrens = zooKeeper.getChildren(REGISTRY_PATH, event -> {
                System.out.println("[Client]:ZooKeeper:event:" + event);
            });
            if (childrens.size() == 0) {
                throw new RuntimeException("没有可用服务器!");
            }
            int index = randomChoose(childrens);
            String data = new String(zooKeeper.getData(REGISTRY_PATH + "/" + childrens.get(index), false, null));
            String[] result = data.split(":"); //得到ip和端口
            map.put("host", result[0]);
            map.put("port", result[1]);
            zooKeeper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * list随机选择一个
     *
     * @param list
     */
    protected int randomChoose(List<String> list) {
        Random random = new Random();
        return random.nextInt(list.size());
    }
}
