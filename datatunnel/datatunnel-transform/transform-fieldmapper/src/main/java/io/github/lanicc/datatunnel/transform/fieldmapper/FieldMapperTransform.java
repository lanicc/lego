package io.github.lanicc.datatunnel.transform.fieldmapper;

import io.github.lanicc.datatunnel.DataObject;
import io.github.lanicc.datatunnel.Transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public class FieldMapperTransform implements Transform {

    private final Map<String, String> fieldMappingMap;

    public FieldMapperTransform(Map<String, String> fieldMappingMap) {
        this.fieldMappingMap = new HashMap<>(fieldMappingMap);
    }

    @Override
    public DataObject transform(DataObject dataObject) {
        fieldMappingMap.forEach((k, mapping) -> {
            Object removed = dataObject.remove(k);
            if (Objects.nonNull(removed)) {
                dataObject.put(mapping, removed);
            }
        });
        return Transform.super.transform(dataObject);
    }
}
