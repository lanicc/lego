package io.github.lanicc.datatunnel;

import java.io.Serializable;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
public interface CommitLog {

    <T extends Serializable> void commit(T log) throws Exception;

    <T extends Serializable> T read() throws Exception;

}
