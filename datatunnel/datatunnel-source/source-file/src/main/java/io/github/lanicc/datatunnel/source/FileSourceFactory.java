package io.github.lanicc.datatunnel.source;

import com.google.auto.service.AutoService;
import io.github.lanicc.datatunnel.CommitLog;
import io.github.lanicc.datatunnel.Source;
import io.github.lanicc.datatunnel.SourceFactory;
import io.github.lanicc.datatunnel.config.Config;
import io.github.lanicc.datatunnel.config.ConfigItem;

import java.util.Collections;
import java.util.List;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@AutoService(SourceFactory.class)
public class FileSourceFactory implements SourceFactory {
    @Override
    public Source create(Config config, CommitLog commitLog) throws Exception {
        return new FileSource(config.getProperty(FileSourceConfigItem.path));
    }

    @Override
    public List<ConfigItem> configItems() {
        return
                Collections.singletonList(
                        FileSourceConfigItem.path
                );
    }
}
