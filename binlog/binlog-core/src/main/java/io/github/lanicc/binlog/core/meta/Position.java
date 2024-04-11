package io.github.lanicc.binlog.core.meta;

import lombok.Data;

import java.io.Serializable;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
@Data
public class Position implements Serializable {

    private String binlogFilename;
    private Long binlogPosition;

    public Position() {
    }

    public Position(String binlogFilename, Long binlogPosition) {
        this.binlogFilename = binlogFilename;
        this.binlogPosition = binlogPosition;
    }
}
