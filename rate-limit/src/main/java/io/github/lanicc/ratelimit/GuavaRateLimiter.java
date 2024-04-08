package io.github.lanicc.ratelimit;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
@SuppressWarnings("UnstableApiUsage")
public class GuavaRateLimiter implements RateLimiter {

    com.google.common.util.concurrent.RateLimiter rateLimiter;

    public GuavaRateLimiter(double permitsPerSecond, long warmupPeriod, TimeUnit unit) {
        rateLimiter = com.google.common.util.concurrent.RateLimiter.create(permitsPerSecond, warmupPeriod, unit);
    }

    @Override
    public boolean acquire() {
        return acquire(1);
    }

    @Override
    public boolean acquire(int permits) {
        return rateLimiter.acquire(permits) == 0.;
    }
}
