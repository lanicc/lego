package io.github.lanicc.datatunnel;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public interface Sink {

    DataConsumer open() throws Exception;

    void close() throws Exception;
}
