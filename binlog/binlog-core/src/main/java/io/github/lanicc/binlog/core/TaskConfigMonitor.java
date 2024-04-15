package io.github.lanicc.binlog.core;

import io.github.lanicc.binlog.core.util.PropertiesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created on 2024/4/11.
 *
 * @author lan
 */
public class TaskConfigMonitor extends AbstractLifeCycle {

    private static final Logger log = LoggerFactory.getLogger(TaskConfigMonitor.class);

    private final TaskController taskController;
    private final Map<String, String> fileDestinationMap = new HashMap<>();

    private FileWatcher fileWatcher;

    public TaskConfigMonitor(TaskController taskController) {
        this.taskController = taskController;
    }

    @Override
    protected void doInit(Properties properties) {
        int interval = PropertiesUtil.getInt(properties, "interval", 1000);
        String destinations = PropertiesUtil.getString(properties, "destinations", "destinations");
        try {
            fileWatcher = new FileWatcher(destinations, interval);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doStart() {
        try {
            fileWatcher.scan();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileWatcher.start();
    }

    private void load(File file) throws IOException {
        log.info("load: {}", file);
        String dest = fileDestinationMap.remove(file.getName());
        if (Objects.nonNull(dest)) {
            taskController.stop(dest);
        }

        String destination = taskController.loadFromFile(file);
        log.info("start: {}", destination);
        fileDestinationMap.put(file.getName(), destination);
    }

    private void remove(String file) {
        log.info("remove: {}", file);
        String dest = fileDestinationMap.remove(file);
        log.info("stop: {}", dest);
        taskController.stop(dest);
    }


    class FileWatcher extends Thread {
        private final WatchService watchService;
        private final WatchKey watchKey;
        private final int interval;
        private final Path dir;

        private final Map<String, Long> fileLastModifiedMap = new HashMap<>();

        public FileWatcher(String path, int interval) throws IOException {
            this.watchService = FileSystems.getDefault().newWatchService();
            this.dir = Paths.get(path);
            log.info("watch dir: {}", dir);
            this.watchKey = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            this.interval = interval;
        }


        private void scan() throws IOException {
            File[] files = dir.toFile().listFiles(file -> file.getName().endsWith(".yml"));
            Map<String, String> fileDestinationMapTmp = new HashMap<>(fileDestinationMap);

            if (Objects.nonNull(files) && ArrayUtils.isNotEmpty(files)) {
                for (File file : files) {
                    String name = file.getName();
                    long lastModified = file.lastModified();
                    Long lastLastModified = fileLastModifiedMap.put(name, lastModified);
                    if (!Objects.equals(lastLastModified, lastModified)) {
                        load(file);
                    }
                    fileDestinationMapTmp.remove(name);
                }
            }
            fileDestinationMapTmp.keySet().forEach(f -> {
                remove(f);
                fileLastModifiedMap.remove(f);
            });
        }

        @Override
        public void run() {
            while (running.get()) {
                try {
                    List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                    if (CollectionUtils.isNotEmpty(watchEvents)) {
                        scan();
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
                try {
                    //noinspection BusyWait
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    log.warn("", e);
                }
            }
        }
    }
}
