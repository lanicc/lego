package io.github.lanicc.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
@Slf4j
class CountBasedRateLimiterTest {

    @Test
    void acquire() throws InterruptedException {
        CountBasedRateLimiter rateLimiter = new CountBasedRateLimiter(1, 5, TimeUnit.SECONDS);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                String tname = Thread.currentThread().getName();
                try {
                    TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(500, 5000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("thread: {}", tname);
                boolean acquired = rateLimiter.acquire();
                if (acquired) {
                    log.info("thread: {} executing...", tname);
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("thread: {} finish.", tname);
                } else {
                    log.info("thread: {} limited.", tname);
                }
            });
        }
        pool.shutdown();
        boolean awaitedTermination = pool.awaitTermination(5, TimeUnit.HOURS);
        log.info("awaitedTermination: {}", awaitedTermination);
    }

}
