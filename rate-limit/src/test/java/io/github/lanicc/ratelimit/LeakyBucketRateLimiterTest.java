package io.github.lanicc.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
class LeakyBucketRateLimiterTest extends RateLimiterTest {

    @Test
    void acquire() {
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(2, 100, TimeUnit.MILLISECONDS);
        run(rateLimiter);
    }
}
