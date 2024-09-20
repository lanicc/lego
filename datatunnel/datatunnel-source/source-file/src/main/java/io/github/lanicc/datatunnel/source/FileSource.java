package io.github.lanicc.datatunnel.source;

import io.github.lanicc.datatunnel.DataConsumer;
import io.github.lanicc.datatunnel.DataObject;
import io.github.lanicc.datatunnel.Source;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

/**
 * Created on 2024/9/18.
 *
 * @author lan
 */
@Slf4j
public class FileSource extends Thread implements Source {

    private final Path path;

    private WatchService watchService;

    private DataConsumer consumer;

    private volatile boolean running;

    public FileSource(String pathStr) throws IOException {
        if (Objects.isNull(pathStr) || pathStr.isEmpty()) {
            throw new IllegalArgumentException(FileSourceConfigItem.path.getKey() + " is null or empty");
        }
        path = Paths.get(pathStr);
    }

    @Override
    public void open(DataConsumer consumer) throws Exception {
        this.consumer = consumer;
        this.watchService = FileSystems.getDefault().newWatchService();

        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_MODIFY
        );

        running = true;

        log.info("Watch path: {}", path);
        start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                WatchKey watchKey = watchService.take();
                for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                    WatchEvent.Kind<?> kind = watchEvent.kind();
                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        Path filename = path.resolve((Path) watchEvent.context());
                        System.out.format("FILE: %s EVENT: %s\n", filename, kind.name());
                        DataObject dataObject = new DataObject();
                        dataObject.put("filename", filename.toString());
                        dataObject.put("kind", kind.name());
                        consumer.consume(dataObject);
                    }
                }
                if (!watchKey.reset()) {
                    throw new RuntimeException("Watch key not reset");
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }

    }

    @Override
    public void close() throws Exception {
        running = false;
        watchService.close();
    }
}
