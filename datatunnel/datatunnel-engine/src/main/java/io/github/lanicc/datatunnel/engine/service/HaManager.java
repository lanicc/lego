package io.github.lanicc.datatunnel.engine.service;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
public interface HaManager {

    void register(String taskName, HaControl haControl) throws Exception;

    interface HaControl {

        void start(String taskName) throws Exception;

        void stop(String taskName);

    }
}
