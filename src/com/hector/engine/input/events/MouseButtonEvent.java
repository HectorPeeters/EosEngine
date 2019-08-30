package com.hector.engine.input.events;

public class MouseButtonEvent {

    public final int button;
    public final boolean pressed;

    public MouseButtonEvent(int button, boolean pressed) {
        this.button = button;
        this.pressed = pressed;
    }
}
