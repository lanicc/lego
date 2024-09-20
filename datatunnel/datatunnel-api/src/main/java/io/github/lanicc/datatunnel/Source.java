package io.github.lanicc.datatunnel;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public interface Source {

    void open(DataConsumer consumer) throws Exception;

    void close() throws Exception;

}
