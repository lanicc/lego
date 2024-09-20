package io.github.lanicc.datatunnel.engine.service.zk;

import io.github.lanicc.datatunnel.engine.service.HaManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
@Slf4j
class ZkHaManagerTest {

    @Test
    @Timeout(100)
    void register() throws Exception {
        CuratorFramework curatorClient = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();
        HaManager haManager = new ZkHaManager(curatorClient);
        haManager.register(
                "lanicc",
                new HaManager.HaControl() {
                    @Override
                    public void start(String taskName) throws Exception {
                        log.info("start: {}", taskName);
                    }

                    @Override
                    public void stop(String taskName) {
                        log.info("stop: {}", taskName);
                    }
                });
        TimeUnit.SECONDS.sleep(100);
    }
}
