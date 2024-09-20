package io.github.lanicc.datatunnel.engine.service;

import io.github.lanicc.datatunnel.CommitLog;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public interface CommitLogManager {

    CommitLog getCommitLog(String taskName, String pluginName) throws Exception;

}
