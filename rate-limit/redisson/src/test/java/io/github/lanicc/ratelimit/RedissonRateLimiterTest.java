package io.github.lanicc.ratelimit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

/**
 * Created on 2024/4/9.
 *
 * @author lan
 */
class RedissonRateLimiterTest extends RateLimiterTest {

    @Test
    @Disabled
    void acquire() {
        RedissonClient redissonClient = Redisson.create();
        RRateLimiter rRateLimiter = redissonClient.getRateLimiter(getClass().getSimpleName());
        rRateLimiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
        RedissonRateLimiter rateLimiter = new RedissonRateLimiter(rRateLimiter);
        run(rateLimiter);
    }
}
