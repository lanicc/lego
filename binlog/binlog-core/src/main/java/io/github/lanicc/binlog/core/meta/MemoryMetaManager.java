package io.github.lanicc.binlog.core.meta;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public class MemoryMetaManager extends AbstractMetaManager {

    private final AtomicReference<Position> positionAtomicReference;

    public MemoryMetaManager(String destination) {
        super(destination);
        positionAtomicReference = new AtomicReference<>();
    }

    @Override
    public Position getCursor() {
        return positionAtomicReference.get();
    }

    @Override
    public void rotate(String binlogFilename, Long binlogPosition) {
        positionAtomicReference.set(new Position(binlogFilename, binlogPosition));
    }

    @Override
    public void updateCursor(Long binlogPosition) {
        positionAtomicReference.get().setBinlogPosition(binlogPosition);
    }
}
