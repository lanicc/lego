package io.github.lanicc.binlog.core.task;

import io.github.lanicc.binlog.core.AbstractLifeCycle;
import io.github.lanicc.binlog.core.event.AbstractEventListener;
import io.github.lanicc.binlog.core.event.EventListener;
import io.github.lanicc.binlog.core.event.LogEventListener;
import io.github.lanicc.binlog.core.meta.AbstractMetaManager;
import io.github.lanicc.binlog.core.meta.MemoryMetaManager;
import io.github.lanicc.binlog.core.meta.MetaManager;
import io.github.lanicc.binlog.core.reader.AbstractBinlogReader;
import io.github.lanicc.binlog.core.reader.BinlogClientReader;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public class TaskInstance extends AbstractLifeCycle {
    private final TaskConfig config;

    private AbstractEventListener eventListener;
    private AbstractMetaManager metaManager;
    private AbstractBinlogReader binlogReader;

    public TaskInstance(TaskConfig config) {
        this.config = config;
    }

    @Override
    protected void doInit(Properties properties) {
        Class<? extends AbstractBinlogReader> binlogReaderImplClass = getClass(config.getBinlogReaderImpl(), BinlogClientReader.class);
        Class<? extends AbstractMetaManager> metaManagerImplClass = getClass(config.getMetaManagerImpl(), MemoryMetaManager.class);
        Class<? extends AbstractEventListener> eventListenerImplClass = getClass(config.getEventListenerImpl(), LogEventListener.class);
        try {
            eventListener =
                    eventListenerImplClass.getConstructor(String.class)
                            .newInstance(config.getDestination());
            eventListener.init(config.getEventListenerConfig());
            metaManager =
                    metaManagerImplClass.getConstructor(String.class)
                            .newInstance(config.getDestination());
            metaManager.init(config.getMetaManagerConfig());
            binlogReader =
                    binlogReaderImplClass.getConstructor(String.class, MetaManager.class, EventListener.class)
                            .newInstance(config.getDestination(), metaManager, eventListener);
            binlogReader.init(config.getBinlogReaderConfig());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> getClass(String className, Class<T> defaultClass) {
        if (StringUtils.isBlank(className)) {
            return defaultClass;
        }
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doStart() {
        new Thread(() -> {
            eventListener.start();
            metaManager.start();
            binlogReader.start();
        }, config.getDestination()).start();
    }

    @Override
    protected void doStop() {
        binlogReader.stop();
        metaManager.stop();
        eventListener.stop();
    }
}
