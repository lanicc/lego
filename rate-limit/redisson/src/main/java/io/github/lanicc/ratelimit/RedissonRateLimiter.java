package io.github.lanicc.ratelimit;

import org.redisson.api.RRateLimiter;

/**
 * Created on 2024/4/9.
 *
 * @author lan
 */
public class RedissonRateLimiter implements RateLimiter {

    private final RRateLimiter rateLimiter;

    public RedissonRateLimiter(RRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean acquire(int permits) {
        return rateLimiter.tryAcquire(permits);
    }
}
