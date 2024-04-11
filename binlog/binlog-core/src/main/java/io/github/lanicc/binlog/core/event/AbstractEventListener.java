package io.github.lanicc.binlog.core.event;

import io.github.lanicc.binlog.core.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public abstract class AbstractEventListener extends AbstractLifeCycle implements EventListener {

    protected final String destination;
    protected final Logger log;

    public AbstractEventListener(String destination) {
        this.destination = destination;
        this.log = LoggerFactory.getLogger(destination);
    }
}
