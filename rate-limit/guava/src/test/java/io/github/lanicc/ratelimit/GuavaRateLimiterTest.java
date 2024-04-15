package io.github.lanicc.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
class GuavaRateLimiterTest extends RateLimiterTest {

    @Test
    void acquire() {
        GuavaRateLimiter rateLimiter = new GuavaRateLimiter(1, 1, TimeUnit.SECONDS);
        run(rateLimiter);
    }
}
