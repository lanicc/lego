package io.github.lanicc.binlog.core.task;

import io.github.lanicc.binlog.core.event.AbstractEventListener;
import io.github.lanicc.binlog.core.meta.AbstractMetaManager;
import io.github.lanicc.binlog.core.reader.AbstractBinlogReader;
import lombok.Data;

import java.util.Properties;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
@Data
public class TaskConfig {

    private String destination;
    private String filter;

    /**
     * @see AbstractBinlogReader
     */
    private String binlogReaderImpl;
    /**
     * @see AbstractMetaManager
     */
    private String metaManagerImpl;
    /**
     * @see AbstractEventListener
     */
    private String eventListenerImpl;

    private Properties binlogReaderConfig;
    private Properties metaManagerConfig;
    private Properties eventListenerConfig;


}
