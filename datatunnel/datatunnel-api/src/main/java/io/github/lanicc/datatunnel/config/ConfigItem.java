package io.github.lanicc.datatunnel.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ConfigItem implements Serializable {

    private String key;
    private String defaultValue;
    public String description;

    @Override
    public String toString() {
        return String.format("配置项: [%s], 默认值: [%s], 说明: %s", key, defaultValue, description);
    }
}
