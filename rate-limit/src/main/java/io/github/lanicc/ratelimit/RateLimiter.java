package io.github.lanicc.ratelimit;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
public interface RateLimiter {

    boolean acquire();

    boolean acquire(int permits);
}
