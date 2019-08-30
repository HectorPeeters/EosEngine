package com.hector.engine.input.events;

public class MouseMoveEvent {

    public final double xPos;
    public final double yPos;
    public final int xPixel;
    public final int yPixel;

    public MouseMoveEvent(double xPos, double yPos, int xPixel, int yPixel) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.xPixel = xPixel;
        this.yPixel = yPixel;
    }
}
