package com.hector.engine.logging;

import com.hector.engine.Engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Logger {

    private static LogType logLevelFilter = LogType.DEBUG;

    private Logger() {
    }

    private static final String ANSI_RESET = "\u001B[0m";

    private static Map<String, BufferedWriter> fileWriters = new HashMap<>();

    private static long startTime;

    public static void init() {
        startTime = System.currentTimeMillis();

        Logger.info("Logger", "Initialized logger");
    }

    public static void debug(String channelTag, Object message) {
        log(channelTag, LogType.DEBUG, message);
    }

    public static void info(String channelTag, Object message) {
        log(channelTag, LogType.INFO, message);
    }

    public static void warn(String channelTag, Object message) {
        log(channelTag, LogType.WARNING, message);
    }

    public static void err(String channelTag, Object message) {
        log(channelTag, LogType.ERROR, message);
    }

    public static void fatal(String channelTag, Object message) {
        log(channelTag, LogType.FATAL, message);
    }

    private static void log(String channelTag, LogType logType, Object message) {
        if (logType.logLevel < logLevelFilter.logLevel)
            return;

        if (!Engine.DEV_BUILD)
            return;

        String fullMessage = (System.currentTimeMillis() - startTime) + " [" + logType.name().charAt(0) + "] [" + channelTag + "] " + message;

        System.out.println(logType.colorPrefix + fullMessage + ANSI_RESET);

//        if (initialized)
//            EventSystem.publish(new LogEvent(fullMessage, logType.logLevel));

        if (!fileWriters.containsKey(channelTag)) {
            try {
                File f = new File("logs/" + channelTag.toLowerCase() + ".log");
                if (!f.exists()) {
                    f.getParentFile().mkdirs();
                    f.createNewFile();
                }

                fileWriters.put(channelTag, new BufferedWriter(new FileWriter(f)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileWriters.get(channelTag).write((System.currentTimeMillis() - startTime) + ": " + fullMessage + "\n");
            fileWriters.get(channelTag).flush();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("Logger", "Failed to write to log file: " + channelTag);
        }
    }

    private static class LogChannel {
        final String tag;
        final boolean debugger;
        final boolean file;

        public LogChannel(String tag, boolean debugger, boolean file) {
            this.tag = tag;
            this.debugger = debugger;
            this.file = file;
        }

        @Override
        public String toString() {
            return "LogChannel{" +
                    "tag='" + tag + '\'' +
                    ", debugger=" + debugger +
                    ", file=" + file +
                    '}';
        }
    }

    private enum LogType {
        DEBUG(0, "\u001B[32m"),
        WARNING(1, "\u001B[33m"),
        INFO(2, "\u001B[0m"),
        ERROR(3, "\u001B[31m"),
        FATAL(4, "\u001B[36m");

        private final int logLevel;
        private final String colorPrefix;

        LogType(int logLevel, String colorPrefix) {
            this.logLevel = logLevel;
            this.colorPrefix = colorPrefix;
        }

        public int getLogLevel() {
            return logLevel;
        }

        public String getColorPrefix() {
            return colorPrefix;
        }
    }

}
