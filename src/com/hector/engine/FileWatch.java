package com.hector.engine;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

public class FileWatch {

    private WatchService watcher;

    public FileWatch(String path, FileWatchListener listener) throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
    }

    static class FileWatchEvent {

    }

    interface FileWatchListener {
        void onEvent(FileWatchEvent event);
    }

}
