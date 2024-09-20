package io.github.lanicc.datatunnel.engine.service.zk;

import org.apache.curator.framework.CuratorFramework;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
public abstract class ZkBasedManager {

    protected final CuratorFramework client;

    public ZkBasedManager(CuratorFramework client) {
        this.client = client;
    }


}
