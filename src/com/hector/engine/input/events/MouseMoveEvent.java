package com.hector.engine.input.events;

public class MouseMoveEvent {

    public final double xpos;
    public final double ypos;
    public final int xpixel;
    public final int ypixel;

    public MouseMoveEvent(double xpos, double ypos, int xpixel, int ypixel) {
        this.xpos = xpos;
        this.ypos = ypos;
        this.xpixel = xpixel;
        this.ypixel = ypixel;
    }
}
