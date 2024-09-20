package io.github.lanicc.datatunnel;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public interface Transform {

    default DataObject transform(DataObject dataObject) {
        return dataObject;
    }

}
