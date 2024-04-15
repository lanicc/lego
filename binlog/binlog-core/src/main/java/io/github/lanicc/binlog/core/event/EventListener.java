package io.github.lanicc.binlog.core.event;

/**
 * Created on 2024/4/10.
 *
 * @author lan
 */
public interface EventListener {

    void onEvent(Event event);
    default void onInsert(Event event) {
        onEvent(event);
    }
    default void onUpdate(Event event) {
        onEvent(event);
    }
    default void onDelete(Event event) {
        onEvent(event);
    }
}
