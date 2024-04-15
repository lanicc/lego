package io.github.lanicc.distributedlock;

import lombok.extern.slf4j.Slf4j;

import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/9.
 *
 * @author lan
 */
@Slf4j
class LockTests {

    volatile int count;
    void runLock(Lock lock) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        count = 0;
        for (int i = 0; i < 100; i++) {
            pool.execute(() -> {
                String name = Thread.currentThread().getName();
                log.info("start {}", name);
                lock.lock();
                log.info("locked {}", name);
                for (int j = 0; j < 1000; j++) {
                    count++;
                }

                log.info("finish {}", name);
                lock.unlock();
            });
        }

        pool.shutdown();
        try {
            boolean awaitTermination = pool.awaitTermination(1, TimeUnit.HOURS);
            log.info("awaitTermination {}", awaitTermination);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (count != 100000) {
            log.error("count {}", count);
            throw new ConcurrentModificationException();
        }
    }
}
