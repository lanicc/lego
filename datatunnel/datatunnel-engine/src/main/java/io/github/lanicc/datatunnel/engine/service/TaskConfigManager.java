package io.github.lanicc.datatunnel.engine.service;

import io.github.lanicc.datatunnel.engine.task.TaskConfig;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created on 2024/9/19.
 *
 * @author lan
 */
@Slf4j
public class TaskConfigManager {


    private final TaskConfigListener listener;
    private final Path configPath;
    private final ScheduledExecutorService executor;

    private final Map<String, Long> configLastModifiedTimeMap;

    public TaskConfigManager(String configPath, TaskConfigListener listener) {
        this.listener = listener;
        this.configPath = Paths.get(configPath);
        this.executor = Executors.newScheduledThreadPool(1);
        this.configLastModifiedTimeMap = new HashMap<>();
    }

    public void start() {
        executor.scheduleAtFixedRate(() -> {
            try {
                monitor();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    private void monitor() throws IOException {
        List<Path> files =
                Arrays.stream(Objects.requireNonNull(configPath.toFile()
                        .list((dir, name) -> name.endsWith(".yml"))))
                .map(configPath::resolve)
                .collect(Collectors.toList());

        files.stream()
                .filter(path -> !configLastModifiedTimeMap.containsKey(path.getFileName().toString()))
                .forEach(this::load);
        files.stream()
                .filter(path -> configLastModifiedTimeMap.containsKey(path.getFileName().toString()))
                .filter(path -> {
                    Long l = configLastModifiedTimeMap.get(path.getFileName().toString());
                    try {
                        return !Objects.equals(Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS).toMillis(), l);
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(this::load);
        Set<String> set =
                files.stream()
                        .map(path -> path.getFileName().toString())
                        .collect(Collectors.toSet());
        configLastModifiedTimeMap.keySet().stream()
                .filter(s -> !set.contains(s))
                .forEach(this::unload);

    }

    private void load(Path path) {
        try {
            TaskConfig taskConfig = new Yaml().loadAs(Files.newInputStream(path), TaskConfig.class);
            listener.load(taskConfig);
            configLastModifiedTimeMap.put(path.getFileName().toString(), Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS).toMillis());
        } catch (Exception e) {
            log.info("load task config error, path: {}", path, e);
        }
    }


    private void unload(String taskConfigFile) {
        try {
            String task = taskConfigFile.replace(".yml", "");
            listener.unload(task);
            configLastModifiedTimeMap.remove(task);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface TaskConfigListener {

        void load(TaskConfig taskConfig) throws Exception;

        void unload(String task) throws Exception;

    }
}
