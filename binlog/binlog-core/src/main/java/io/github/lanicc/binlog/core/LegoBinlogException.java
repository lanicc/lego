package io.github.lanicc.binlog.core;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public class LegoBinlogException extends RuntimeException {
    public LegoBinlogException() {
    }

    public LegoBinlogException(String message) {
        super(message);
    }

    public LegoBinlogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LegoBinlogException(Throwable cause) {
        super(cause);
    }

    public LegoBinlogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
