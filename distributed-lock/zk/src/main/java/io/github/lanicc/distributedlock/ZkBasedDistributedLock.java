package io.github.lanicc.distributedlock;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * Created on 2024/4/9.
 *
 * @author lan
 */
@Slf4j
public class ZkBasedDistributedLock implements Lock {

    private final InterProcessMutex lock;

    public ZkBasedDistributedLock(InterProcessMutex lock) {
        this.lock = lock;
    }

    @SneakyThrows
    @Override
    public void lock() {
        lock.acquire();
    }

    @SneakyThrows
    @Override
    public void unlock() {
        lock.release();
    }
}
