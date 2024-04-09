package io.github.lanicc.distributedlock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Created on 2024/4/9.
 *
 * @author lan
 */
@Disabled
class ZkBasedDistributedLockTest extends LockTests {
    static CuratorFramework client;

    @BeforeAll
    static void beforeAll() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
        client.start();
    }

    @AfterAll
    static void afterAll() {
        client.close();
    }

    @Test
    void lock() {
        Lock lock = new ZkBasedDistributedLock(new InterProcessMutex(client, "/lock/" + getClass().getSimpleName()));
        runLock(lock);
    }

    @Test
    void unlock() {
    }
}
