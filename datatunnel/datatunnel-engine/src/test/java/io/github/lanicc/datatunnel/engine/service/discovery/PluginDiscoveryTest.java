package io.github.lanicc.datatunnel.engine.service.discovery;

import io.github.lanicc.datatunnel.SinkFactory;
import io.github.lanicc.datatunnel.SourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Slf4j
class PluginDiscoveryTest {

    @Test
    void getPluginInstances() {
        PluginDiscovery<SinkFactory> sinkFactoryPluginDiscovery = new PluginDiscovery<>(SinkFactory.class);
        List<SinkFactory> pluginInstances = sinkFactoryPluginDiscovery.getPluginInstances();
        log.info("{}", pluginInstances);
        PluginDiscovery<SourceFactory> sourceFactoryPluginDiscovery = new PluginDiscovery<>(SourceFactory.class);
        List<SourceFactory> pluginInstances1 = sourceFactoryPluginDiscovery.getPluginInstances();
        log.info("{}", pluginInstances1);
    }
}
