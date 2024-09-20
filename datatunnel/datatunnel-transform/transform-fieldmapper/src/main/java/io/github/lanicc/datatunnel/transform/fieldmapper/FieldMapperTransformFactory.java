package io.github.lanicc.datatunnel.transform.fieldmapper;

import com.google.auto.service.AutoService;
import io.github.lanicc.datatunnel.CommitLog;
import io.github.lanicc.datatunnel.Transform;
import io.github.lanicc.datatunnel.TransformFactory;
import io.github.lanicc.datatunnel.config.Config;

import java.util.Map;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@AutoService(TransformFactory.class)
public class FieldMapperTransformFactory implements TransformFactory {
    @Override
    public Transform create(Config config, CommitLog commitLog) throws Exception {
        Map<String, String> configAsMap = config.getAsMap(FieldMapperTransformConfigItem.mappings);
        return new FieldMapperTransform(configAsMap);
    }
}
