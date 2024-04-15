package io.github.lanicc.ratelimit;

/**
 * Created on 2024/4/8.
 *
 * @author lan
 */
public interface RateLimiter {

    default boolean acquire() {
        return acquire(1);
    }

    boolean acquire(int permits);
}
