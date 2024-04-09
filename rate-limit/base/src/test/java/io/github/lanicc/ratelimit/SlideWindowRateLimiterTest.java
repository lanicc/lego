package io.github.lanicc.ratelimit;

import org.junit.jupiter.api.Test;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
class SlideWindowRateLimiterTest extends RateLimiterTest {

    @Test
    void acquire() {
        SlideWindowRateLimiter rateLimiter = new SlideWindowRateLimiter(5, 5);
        run(rateLimiter);
    }
}
