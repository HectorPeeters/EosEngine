package com.hector.engine.input.events;

public class MouseMoveEvent {

    public double xpos;
    public double ypos;

    public MouseMoveEvent(double xpos, double ypos) {
        this.xpos = xpos;
        this.ypos = ypos;
    }
}
