package io.github.lanicc.ratelimit;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于计数器的限流
 * <p>
 * Created on 2024/4/8.
 *
 * @author lan
 */
public class CountBasedRateLimiter implements RateLimiter {

    private final int limit;

    private final long windowSize;

    private final AtomicInteger counter;

    private volatile long windowStartTime;

    public CountBasedRateLimiter(int limit) {
        this(limit, 1, TimeUnit.SECONDS);
    }

    public CountBasedRateLimiter(int limit, long windowSize, @Nullable TimeUnit unit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be greater than 0");
        }
        this.limit = limit;
        if (windowSize <= 0) {
            throw new IllegalArgumentException("windowSize must be greater than 0");
        }
        if (unit == null) {
            unit = TimeUnit.MILLISECONDS;
        }
        this.windowSize = unit.toMillis(windowSize);
        this.counter = new AtomicInteger(limit);
        reset();
    }

    @Override
    public boolean acquire(int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("permits must be greater than 0");
        }
        checkWindow();
        int c;
        while ((c = counter.get()) >= permits) {
            if (counter.compareAndSet(c, c - permits)) {
                return true;
            }
        }
        return false;
    }

    private void checkWindow() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis >= windowStartTime + windowSize) {
            reset();
        }
    }

    private void reset() {
        windowStartTime = System.currentTimeMillis();
        counter.set(limit);
    }
}
