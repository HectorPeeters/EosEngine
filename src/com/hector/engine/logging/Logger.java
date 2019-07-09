package com.hector.engine.logging;

import com.hector.engine.Engine;
import com.hector.engine.event.EventSystem;
import com.hector.engine.logging.events.LogEvent;

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

    private static boolean initialized = false;

    public static void init() {
        startTime = System.currentTimeMillis();

        Logger.info("Logger", "Initialized logger");

        initialized = true;
    }

   /* private static boolean loadConfig(String configFile) {
        logChannels.clear();

        XMLLoader loader = new XMLLoader(configFile);
        if (!loader.load()) {
            System.err.println("Failed to load log file: " + configFile);
            return false;
        }

        Element root = loader.getDocumentElement();
        if (!root.getNodeName().equals("Logging")) {
            System.err.println("Failed to load log config file: Root node incorrect");
            return false;
        }

        logLevelFilter = 0;
        if (root.hasAttribute("filter_level")) {
            try {
                logLevelFilter = Integer.parseInt(root.getAttribute("filter_level"));
            } catch (NumberFormatException ignored) {
                Logger.warn("Logger", "Invalid log level filter value");
            }
        }

        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);

            if (!node.getNodeName().equals("Log"))
                continue;

            Element element = (Element) node;

            String tag = element.getAttribute("channel");
            boolean debugger = element.getAttribute("debugger").equals("1");
            boolean file = element.getAttribute("file").equals("1");

            LogChannel logChannel = new LogChannel(tag, debugger, file);

            logChannels.put(tag, logChannel);

            if (file) {
                try {
                    fileWriters.put(tag, new BufferedWriter(new FileWriter(new File("logs/" + tag.toLowerCase() + ".log"))));
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.err("Logger", "Failed to create FileWriter for log channel: " + tag);
                }
            }
        }

        return true;
    }*/

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

    public static void fatal(String channelTag, Exception e, Object message) {
        err(channelTag, message);
    }

    private static void log(String channelTag, LogType logType, Object message) {
        if (logType.logLevel < logLevelFilter.logLevel)
            return;

        if (!Engine.DEV_BUILD)
            return;

        String fullMessage = "[" + logType.name().charAt(0) + "] [" + channelTag + "] " + message;

        System.out.println(logType.colorPrefix + fullMessage + ANSI_RESET);

        if (initialized)
            EventSystem.publish(new LogEvent(fullMessage, logType.logLevel));

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
        ERROR(3, "\u001B[31m");

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
