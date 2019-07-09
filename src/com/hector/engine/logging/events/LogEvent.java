package com.hector.engine.logging.events;

public class LogEvent {

    public String message;
    public int level;

    public LogEvent(String message, int level) {
        this.message = message;
        this.level = level;
    }
}
