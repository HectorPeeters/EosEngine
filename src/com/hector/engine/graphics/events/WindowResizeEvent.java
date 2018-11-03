package com.hector.engine.graphics.events;

public class WindowResizeEvent {

    public final int oldWidth, oldHeight;
    public final int newWidth, newHeight;

    public WindowResizeEvent(int oldWidth, int oldHeight, int newWidth, int newHeight) {
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
    }
}
