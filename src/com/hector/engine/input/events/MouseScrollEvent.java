package com.hector.engine.input.events;

public class MouseScrollEvent {

    public final float xScroll;
    public final float yScroll;

    public MouseScrollEvent(float xScroll, float yScroll) {
        this.xScroll = xScroll;
        this.yScroll = yScroll;
    }
}
