package io.github.lanicc.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
@Slf4j
class CountBasedRateLimiterTest extends RateLimiterTest {

    @Test
    void acquire() throws InterruptedException {
        CountBasedRateLimiter rateLimiter = new CountBasedRateLimiter(1, 5, TimeUnit.SECONDS);
        run(rateLimiter);
    }

}
