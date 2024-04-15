package io.github.lanicc.binlog.core;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public abstract class AbstractLifeCycle implements LifeCycle {

    protected final AtomicBoolean running = new AtomicBoolean(false);
    protected final AtomicBoolean stopped = new AtomicBoolean(false);
    protected final AtomicBoolean inited = new AtomicBoolean(false);

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            doStart();
        }
    }

    @Override
    public void stop() {
        if (stopped.compareAndSet(false, true)) {
            doStop();
        }
    }

    public void init(Properties properties) {
        if (inited.compareAndSet(false, true)) {
            doInit(properties);
        }
    }

    protected void doInit(Properties properties) {};

    protected void doStart() {};

    protected void doStop() {};

}
