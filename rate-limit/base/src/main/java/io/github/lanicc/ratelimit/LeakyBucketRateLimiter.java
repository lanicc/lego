package io.github.lanicc.ratelimit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 漏桶算法限流
 * <p>
 * Created on 2024/4/8.
 *
 * @author lan
 */
@Slf4j
public class LeakyBucketRateLimiter implements RateLimiter {

    private final ScheduledExecutorService executor;

    private final int bucketSize;

    private final ArrayBlockingQueue<Object> bucket;

    public LeakyBucketRateLimiter(int bucketSize, int rate, TimeUnit unit) {
        this.bucketSize = bucketSize;
        this.bucket = new ArrayBlockingQueue<>(bucketSize);
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::leak, 0, rate, unit);
    }

    private void leak() {
        Object polled = bucket.poll();
        if (polled != null) {
            synchronized (polled) {
                log.info("notify");
                polled.notify();
            }
        }
    }

    @SneakyThrows
    @Override
    public boolean acquire(int permits) {
        Object o = new Object();
        synchronized (o) {
            if (bucket.offer(o)) {
                log.info("wait");
                o.wait();
                return true;
            }
        }
        return false;
    }
}
