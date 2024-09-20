package io.github.lanicc.datatunnel.sink.log;

import io.github.lanicc.datatunnel.DataConsumer;
import io.github.lanicc.datatunnel.Sink;
import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Slf4j
public class LogSink implements Sink {


    @Override
    public DataConsumer open() throws Exception {
        return dataObject -> log.info(dataObject.toString());
    }

    @Override
    public void close() throws Exception {

    }
}
