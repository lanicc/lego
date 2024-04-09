package io.github.lanicc.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
class TokenBucketRateLimiterTest extends RateLimiterTest {

    @Test
    void acquire() {
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(2, 100, TimeUnit.MILLISECONDS);
        run(rateLimiter);
    }
}
