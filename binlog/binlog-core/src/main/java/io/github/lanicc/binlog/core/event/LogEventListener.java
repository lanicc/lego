package io.github.lanicc.binlog.core.event;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public class LogEventListener extends AbstractEventListener {


    public LogEventListener(String destination) {
        super(destination);
    }

    @Override
    public void onEvent(Event event) {
        log.info("{}", event);
    }
}
