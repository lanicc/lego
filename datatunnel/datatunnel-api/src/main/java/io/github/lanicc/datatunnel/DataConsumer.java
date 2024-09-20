package io.github.lanicc.datatunnel;

import java.util.Collection;
import java.util.Objects;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public interface DataConsumer {

    void consume(DataObject dataObject);

    default void consume(Collection<DataObject> dataObjects) {
        if (Objects.nonNull(dataObjects)) {
            dataObjects.forEach(this::consume);
        }
    }
}
