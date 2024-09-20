package io.github.lanicc.datatunnel.source;

import com.google.auto.service.AutoService;
import io.github.lanicc.datatunnel.CommitLog;
import io.github.lanicc.datatunnel.Source;
import io.github.lanicc.datatunnel.SourceFactory;
import io.github.lanicc.datatunnel.config.Config;
import io.github.lanicc.datatunnel.config.ConfigItem;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@AutoService(SourceFactory.class)
public class CdcMysqlSourceFactory implements SourceFactory {
    @Override
    public Source create(Config config, CommitLog commitLog) throws Exception {
        return new CdcMysqlSource(config, commitLog);
    }

    @Override
    public List<ConfigItem> configItems() {
        return
                Arrays.asList(
                        CdcMysqlSourceConfigItem.hostname,
                        CdcMysqlSourceConfigItem.port,
                        CdcMysqlSourceConfigItem.username,
                        CdcMysqlSourceConfigItem.password,
                        CdcMysqlSourceConfigItem.serverId,
                        CdcMysqlSourceConfigItem.keepAlive,
                        CdcMysqlSourceConfigItem.binlogFilename,
                        CdcMysqlSourceConfigItem.binlogPosition
                );
    }
}
