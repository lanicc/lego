package io.github.lanicc.datatunnel;

import io.github.lanicc.datatunnel.config.Config;
import io.github.lanicc.datatunnel.config.ConfigItem;

import java.util.Collections;
import java.util.List;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public interface Factory<T> {

    T create(Config config, CommitLog commitLog) throws Exception;

    default List<ConfigItem> configItems() {
        return Collections.emptyList();
    }

    default String getName() {
        return this.getClass().getSimpleName();
    }
}
