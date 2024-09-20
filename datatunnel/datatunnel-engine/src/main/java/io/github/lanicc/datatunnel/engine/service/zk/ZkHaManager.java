package io.github.lanicc.datatunnel.engine.service.zk;

import io.github.lanicc.datatunnel.engine.service.HaManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
@Slf4j
public class ZkHaManager extends ZkBasedManager implements HaManager {

    private final String peerId;

    public ZkHaManager(CuratorFramework client) {
        super(client);
        peerId = UUID.randomUUID().toString();
    }

    @Override
    public void register(String taskName, HaControl haControl) throws Exception {
        HaWatcher haWatcher = new HaWatcher(taskName, haControl);
        haWatcher.start();
    }

    class HaWatcher implements CuratorWatcher {

        private final String path;
        private final String taskName;
        private final HaControl haControl;

        HaWatcher(String taskName, HaControl haControl) {
            this.path = "/datatunnel/ha/" + taskName;
            this.taskName = taskName;
            this.haControl = haControl;
        }

        public void start() throws Exception {
            tryCreateNode();
            client.watchers()
                    .add()
                    .withMode(AddWatchMode.PERSISTENT)
                    .usingWatcher(this)
                    .forPath(path);
            shouldStart();
        }

        @Override
        public void process(WatchedEvent event) throws Exception {
            log.info("Watched event: {}, path: {}", event, path);
            if (event.getType() == Watcher.Event.EventType.NodeCreated) {
                shouldStart();
            } else if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
                tryCreateNode();
                shouldStart();
            }
        }

        private void tryCreateNode() throws Exception {
            try {
                client.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.EPHEMERAL)
                        .forPath(path, peerId.getBytes(StandardCharsets.UTF_8));
            } catch (KeeperException.NodeExistsException e) {
                log.warn("NodeExists: {}", path);
            }
        }

        private void shouldStart() throws Exception {
            byte[] bytes = client.getData().forPath(path);
            if (Objects.nonNull(bytes)) {
                String nodePeerId = new String(bytes, StandardCharsets.UTF_8);
                if (Objects.equals(nodePeerId, peerId)) {
                    haControl.start(taskName);
                }
            }
        }
    }
}
