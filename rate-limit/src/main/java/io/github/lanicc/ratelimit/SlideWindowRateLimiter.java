package io.github.lanicc.ratelimit;

/**
 * 滑动窗口限流
 * <p>
 * Created on 2024/4/8.
 *
 * @author lan
 */
public class SlideWindowRateLimiter implements RateLimiter {

    private final int windowSize;
    private final int limit;
    private final CountBasedRateLimiter[] rateLimiters;

    public SlideWindowRateLimiter(int windowSize, int limit) {
        this.windowSize = windowSize;
        this.limit = limit;
        rateLimiters = new CountBasedRateLimiter[windowSize];
    }

    @Override
    public boolean acquire(int permits) {
        CountBasedRateLimiter rateLimiter = getRateLimiter();
        return rateLimiter.acquire(permits);
    }

    private CountBasedRateLimiter getRateLimiter() {
        long currentTimeMillis = System.currentTimeMillis();
        int idx = Math.toIntExact((currentTimeMillis / 1000) % windowSize);
        if (rateLimiters[idx] == null) {
            synchronized (this) {
                if (rateLimiters[idx] == null) {
                    rateLimiters[idx] = new CountBasedRateLimiter(limit / windowSize);
                }
            }
        }
        return rateLimiters[idx];
    }
}
