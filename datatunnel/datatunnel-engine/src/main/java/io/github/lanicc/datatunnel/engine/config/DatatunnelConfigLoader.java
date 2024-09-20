package io.github.lanicc.datatunnel.engine.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
public class DatatunnelConfigLoader {

    public static DatatunnelConfig getDatatunnelConfig() {
        String configFile = System.getProperty("datatunnel.config");
        if (Objects.isNull(configFile)) {
            return new DatatunnelConfig();
        }
        try {
            return new Yaml().loadAs(Files.newInputStream(Paths.get(configFile)), DatatunnelConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
