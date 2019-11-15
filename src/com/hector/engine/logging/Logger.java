package com.hector.engine.logging;

import com.hector.engine.Engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Logger class for logging debug, info, warning and error messages to the console and a file.
 * @author HectorPeeters
 */
public final class Logger {

    /**
     * The log level filter of the logger. Log messages with a lower log level won't be shown.
     */
    private static LogType logLevelFilter = LogType.DEBUG;

    /**
     * Private constructor to prevent initialisation.
     */
    private Logger() { }

    /**
     * The ansi color reset constant.
     */
    private static final String ANSI_RESET = "\u001B[0m";

    /**
     * A map containing all channels and file writers.
     */
    private static Map<String, BufferedWriter> fileWriters = new HashMap<>();

    /**
     * The start time of the logger. Used for printing the time next to log message.
     */
    private static long startTime;

    /**
     * Init method of the logger class which just sets the start time.
     */
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

    /**
     * Generic log method which prints all log messages.
     * @param channelTag    The channel to log the message to
     * @param logType       The type of log message (log level)
     * @param message       The message to log
     */
    private static void log(String channelTag, LogType logType, Object message) {
        // If not in development build don't print message
        if (!Engine.DEV_BUILD)
            return;

        // Check if the message has to be logged
        if (logType.logLevel < logLevelFilter.logLevel)
            return;

        // The full message with all formatting and color
        String fullMessage = (System.currentTimeMillis() - startTime) + " [" + logType.name().charAt(0) + "] [" + channelTag + "] " + message;

        // Printing the message to the console
        System.out.println(logType.colorPrefix + fullMessage + ANSI_RESET);

//        if (initialized)
//            EventSystem.publish(new LogEvent(fullMessage, logType.logLevel));

        // Check if there is a file writer for the current channel
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

        // Write the log message to the corresponding file
        try {
            fileWriters.get(channelTag).write((System.currentTimeMillis() - startTime) + ": " + fullMessage + "\n");
            fileWriters.get(channelTag).flush();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("Logger", "Failed to write to log file: " + channelTag);
        }
    }

    /**
     *  Class representing a log channel with a tag, and flags whether or not to log to a file or not
     */
    private static class LogChannel {
        /**
         * The tag of the log channel
         */
        final String tag;

        /**
         * Whether or not this channel has to be logged to a file or not
         */
        final boolean file;

        /**
         * Basic constructor which just sets the fields
         * @param tag   The tag of the logger
         * @param file  The file log flag
         */
        public LogChannel(String tag, boolean file) {
            this.tag = tag;
            this.file = file;
        }

        /**
         * Method to convert {@link LogChannel} to {@link String}
         * @return A {@link String} representation of the {@link LogChannel}
         */
        @Override
        public String toString() {
            return "LogChannel{" +
                    "tag='" + tag + '\'' +
                    ", file=" + file +
                    '}';
        }
    }

    /**
     * An enum representing all the log levels and their corresponding colors.
     */
    private enum LogType {
        DEBUG(0, "\u001B[32m"),
        WARNING(1, "\u001B[33m"),
        INFO(2, "\u001B[0m"),
        ERROR(3, "\u001B[31m"),
        FATAL(4, "\u001B[36m");

        /**
         * An integer representing the log level for comparing them.
         */
        private final int logLevel;

        /**
         * The color prefix of the log level
         */
        private final String colorPrefix;

        /**
         * Basic constructor which sets the fields
         * @param logLevel
         * @param colorPrefix
         */
        LogType(int logLevel, String colorPrefix) {
            this.logLevel = logLevel;
            this.colorPrefix = colorPrefix;
        }

        /**
         * Getter for the logLevel int
         * @return The log level int of the LogType
         */
        public int getLogLevel() {
            return logLevel;
        }

        /**
         * Getter for the color prefix
         * @return The color prefix of the LogType
         */
        public String getColorPrefix() {
            return colorPrefix;
        }
    }

}
