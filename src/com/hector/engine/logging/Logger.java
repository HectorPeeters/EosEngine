package com.hector.engine.logging;

import com.hector.engine.xml.XMLLoader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Logger {

    private static int logLevelFilter;

    private Logger() {
    }

    private static final String ANSI_RESET = "\u001B[0m";

    private static Map<String, LogChannel> logChannels = new HashMap<>();

    private static Map<String, BufferedWriter> fileWriters = new HashMap<>();

    public static void init(String configFile) {
        boolean loadedConfig = loadConfig(configFile);
        if (!loadedConfig)
            return;

        Logger.info("Logger", "Initialized logger");
    }

    private static boolean loadConfig(String configFile) {
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

            String tag = element.getAttribute("tag");
            boolean debugger = element.getAttribute("debugger").equals("1");
            boolean file = element.getAttribute("file").equals("1");

            LogChannel logChannel = new LogChannel(tag, debugger, file);

            logChannels.put(tag, logChannel);

            if (file) {
                //TODO: put "logs/" in config file
                try {
                    fileWriters.put(tag, new BufferedWriter(new FileWriter(new File("logs/" + tag.toLowerCase() + ".log"))));
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.err("Logger", "Failed to create FileWriter for log channel: " + tag);
                }
            }
        }

        return true;
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

    private static void log(String channelTag, LogType logType, Object message) {
        if (logType.logLevel < logLevelFilter)
            return;

        LogChannel logChannel = logChannels.get(channelTag);

        if (logChannel == null) {
            System.err.println("LogChannel " + channelTag + " is not defined in config file");
            return;
        }


        String fullMessage = "[" + logType.name().charAt(0) + "] [" + logChannel.tag + "] " + message;

        if (logChannel.debugger)
            System.out.println(logType.colorPrefix + fullMessage + ANSI_RESET);

        if (logChannel.file && fileWriters.containsKey(channelTag)) {
            try {
                fileWriters.get(channelTag).write(fullMessage + "\n");
                //TODO: optimize this because flush() is pretty slow
                fileWriters.get(channelTag).flush();
            } catch (IOException e) {
                e.printStackTrace();
                Logger.err("Logger", "Failed to write to log file: " + channelTag);
            }
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
