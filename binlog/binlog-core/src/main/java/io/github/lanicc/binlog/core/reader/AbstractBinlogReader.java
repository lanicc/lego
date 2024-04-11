package io.github.lanicc.binlog.core.reader;

import io.github.lanicc.binlog.core.AbstractLifeCycle;
import io.github.lanicc.binlog.core.event.EventListener;
import io.github.lanicc.binlog.core.meta.MetaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public abstract class AbstractBinlogReader extends AbstractLifeCycle implements BinlogReader {
    protected final String destination;
    protected final MetaManager metaManager;
    protected final EventListener eventListener;
    protected final Logger log;

    public AbstractBinlogReader(String destination, MetaManager metaManager, EventListener eventListener) {
        this.destination = destination;
        this.metaManager = metaManager;
        this.eventListener = eventListener;
        this.log = LoggerFactory.getLogger(destination);
    }
}
