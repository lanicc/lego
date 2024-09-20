package io.github.lanicc.datatunnel.engine;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.Test;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
public class CuratorTests {

    @Test
    void create() throws Exception {
        CuratorFramework curatorClient = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();
        String s = curatorClient.create().creatingParentsIfNeeded().forPath("/1/2/3/4");
        System.out.println(s);
    }
}
