package com.hector.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileWatchTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        Path p = new File("assets/").toPath();
//        WatchKey k = p.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

        registerAll(watcher, p);

        while (true) {
            WatchKey key = watcher.take();

            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.OVERFLOW)
                    continue;

                WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

                System.out.println(pathEvent.context());
            }

            if (!key.reset()) {
                break;
            }
        }
    }

    private static void registerAll(WatchService watcher, final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }

        });

    }

}
