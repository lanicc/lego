package io.github.lanicc.datatunnel.engine.service.zk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.github.lanicc.datatunnel.CommitLog;
import io.github.lanicc.datatunnel.engine.service.CommitLogManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
@Slf4j
public class ZkCommitLogManager extends ZkBasedManager implements CommitLogManager {

    public ZkCommitLogManager(CuratorFramework client) {
        super(client);
    }

    public CommitLog getCommitLog(String taskName, String pluginName) throws Exception {
        try {
            client.create().creatingParentsIfNeeded().forPath(getNodePath(taskName, pluginName), new byte[0]);
        } catch (KeeperException.NodeExistsException e) {
            // ignore
        }
        return new CommitLog() {
            @Override
            public <T extends Serializable> void commit(T log) throws Exception {
                ZkCommitLogManager.this.commit(taskName, pluginName, log);
            }

            @Override
            public <T extends Serializable> T read() throws Exception {
                return ZkCommitLogManager.this.read(taskName, pluginName);
            }
        };
    }


    private void commit(String taskName, String pluginName, Serializable commitLog) throws Exception {
        log.info("{}-{}, commit: {}", taskName, pluginName, commitLog);
        client.setData().forPath(getNodePath(taskName, pluginName), JSON.toJSONString(commitLog, SerializerFeature.WriteClassName).getBytes(StandardCharsets.UTF_8));
    }

    private <T> T read(String taskName, String pluginName) throws Exception {
        byte[] bytes = null;
        try {
            bytes = client.getData().forPath(getNodePath(taskName, pluginName));
        } catch (KeeperException.NoNodeException e) {
            // ignore
        }
        if (Objects.nonNull(bytes) && bytes.length > 0) {
            ParserConfig parserConfig = new ParserConfig();
            parserConfig.setAutoTypeSupport(true);

            return JSON.parseObject(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8, Serializable.class, parserConfig);
        }
        return null;
    }


    private String getNodePath(String taskName, String pluginName) {
        return "/datatunnel/task/" + taskName + "/" + pluginName;
    }
}
