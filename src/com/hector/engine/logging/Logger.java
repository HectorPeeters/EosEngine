package com.hector.engine.logging;

import com.hector.engine.xml.XMLLoader;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

//TODO: add log to file functionality
//TODO: add log filter
public final class Logger {

    private Logger() { }

    private static final String ANSI_RESET = "\u001B[0m";

    private static Map<String, LogChannel> logChannels = new HashMap<>();

    public static void init(String configFile) {
        boolean loadedConfig = loadConfig(configFile);
        if (!loadedConfig)
            return;
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
        LogChannel logChannel = logChannels.get(channelTag);

        if (logChannel == null) {
            System.err.println("LogChannel " + channelTag + " is not defined in config file");
            return;
        }

        if (logChannel.debugger)
            System.out.println(logType.colorPrefix + "[" + logType.name().charAt(0) + "] [" + logChannel.tag + "] " + message + ANSI_RESET);
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
