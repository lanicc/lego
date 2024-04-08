package io.github.lanicc.ratelimit;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 漏桶算法限流
 * <p>
 * Created on 2024/4/8.
 *
 * @author lan
 */
@Slf4j
public class LeakyBucketRateLimiter implements RateLimiter {

    private final AtomicInteger bucket;

    private final ScheduledExecutorService executor;

    private final int bucketSize;

    public LeakyBucketRateLimiter(int bucketSize, int rate, TimeUnit unit) {
        this.bucketSize = bucketSize;
        this.bucket = new AtomicInteger(bucketSize);
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::leak, 0, rate, unit);
    }

    private void leak() {
        if (bucket.get() < bucketSize) {
            int i = bucket.incrementAndGet();
            log.info("leak bucket:{}", i);
        }
    }

    @Override
    public boolean acquire() {
        return acquire(1);
    }

    @Override
    public boolean acquire(int permits) {
        int c;
        while ((c = bucket.get()) >= permits) {
            if (bucket.compareAndSet(c, c - permits)) {
                return true;
            }
        }
        return false;
    }
}
