package io.github.lanicc.datatunnel.engine.service;

import io.github.lanicc.datatunnel.Factory;
import io.github.lanicc.datatunnel.engine.service.discovery.PluginDiscovery;

import java.util.*;
import java.util.function.Predicate;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public class PluginManager {

    private final Map<Class<? extends Factory<?>>, PluginDiscovery<? extends Factory<?>>> pluginDiscoveries = new HashMap<>();
    private final Map<Class<? extends Factory<?>>, List<? extends Factory<?>>> pluginInstances = new HashMap<>();

    public <T extends Factory<?>> T getPlugin(Class<T> clazz, String name) {
        List<? extends Factory<?>> objects = pluginInstances.get(clazz);
        if (objects == null) {
            PluginDiscovery<? extends Factory<?>> pluginDiscovery = pluginDiscoveries.computeIfAbsent(clazz, PluginDiscovery::new);
            List<? extends Factory<?>> discoveryPluginInstances = pluginDiscovery.getPluginInstances();
            if (Objects.isNull(discoveryPluginInstances)) {
                pluginInstances.put(clazz, Collections.emptyList());
            } else {
                pluginInstances.put(clazz, discoveryPluginInstances);
            }
            objects = pluginInstances.get(clazz);
        }
        Factory<?> factory =
                objects.stream()
                        .filter((Predicate<Factory<?>>) factory1 -> Objects.equals(factory1.getName(), name))
                        .findFirst()
                        .orElse(null);

        if (Objects.isNull(factory)) {
            throw new NullPointerException(name + " not found");
        }
        //noinspection unchecked
        return (T) factory;
    }
}
