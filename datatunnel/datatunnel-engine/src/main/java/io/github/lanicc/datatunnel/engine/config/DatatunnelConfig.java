package io.github.lanicc.datatunnel.engine.config;

import lombok.Data;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
@Data
public class DatatunnelConfig {

    private String zkUrl;

    private String taskConfigPath;
}
