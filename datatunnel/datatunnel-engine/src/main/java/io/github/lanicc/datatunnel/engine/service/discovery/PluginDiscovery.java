package io.github.lanicc.datatunnel.engine.service.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public class PluginDiscovery<T> {
    private final Class<T> pluginClass;

    public PluginDiscovery(Class<T> pluginClass) {
        this.pluginClass = pluginClass;
    }

    public Class<?> getPluginBaseClass() {
        return pluginClass;
    }

    public List<T> getPluginInstances() {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(pluginClass);
        List<T> plugins = new ArrayList<>();
        serviceLoader.iterator().forEachRemaining(plugins::add);
        return plugins;
    }
}
