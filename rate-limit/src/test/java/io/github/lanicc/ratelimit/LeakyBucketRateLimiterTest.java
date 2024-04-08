package io.github.lanicc.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
class LeakyBucketRateLimiterTest extends RateLimiterTest {

    @Test
    void acquire() {
        LeakyBucketRateLimiter rateLimiter = new LeakyBucketRateLimiter(1, 1, TimeUnit.SECONDS);
        run(rateLimiter);
    }
}
