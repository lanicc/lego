package io.github.lanicc.datatunnel.source;

import lombok.Data;

import java.io.Serializable;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Data
public class CommittedLog implements Serializable {

    private String binlogFilename;

    private Long binlogPosition;

    public CommittedLog() {
    }

    public CommittedLog(String binlogFilename, Long binlogPosition) {
        this.binlogFilename = binlogFilename;
        this.binlogPosition = binlogPosition;
    }
}
