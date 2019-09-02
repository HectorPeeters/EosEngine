package com.hector.engine.input.events;

public class KeyEvent {

    public final int keycode;
    public final boolean pressed;

    public KeyEvent(int keycode, boolean pressed) {
        this.keycode = keycode;
        this.pressed = pressed;
    }
}
