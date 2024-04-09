package io.github.lanicc.distributedlock;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2024/4/9.
 *
 * @author lan
 */
public interface Lock {

    void lock();

    void unlock();

    default boolean tryLock(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

}
