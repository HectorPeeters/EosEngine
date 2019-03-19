package com.hector.engine.input.events;

public class MouseButtonEvent {

    public int button;
    public boolean pressed;

    public MouseButtonEvent(int button, boolean pressed) {
        this.button = button;
        this.pressed = pressed;
    }
}
