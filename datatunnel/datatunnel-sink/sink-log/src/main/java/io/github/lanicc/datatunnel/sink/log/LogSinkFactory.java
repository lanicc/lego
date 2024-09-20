package io.github.lanicc.datatunnel.sink.log;

import com.google.auto.service.AutoService;
import io.github.lanicc.datatunnel.CommitLog;
import io.github.lanicc.datatunnel.Sink;
import io.github.lanicc.datatunnel.SinkFactory;
import io.github.lanicc.datatunnel.config.Config;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@AutoService(SinkFactory.class)
public class LogSinkFactory implements SinkFactory {


    @Override
    public Sink create(Config config, CommitLog commitLog) {
        return new LogSink();
    }
}
