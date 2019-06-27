package com.hector.engine.graphics.events;

public class WindowResizeEvent {

    public final int width, height;

    public WindowResizeEvent(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
