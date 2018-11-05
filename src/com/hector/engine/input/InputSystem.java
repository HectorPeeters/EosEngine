package com.hector.engine.input;

import com.hector.engine.event.Handler;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.systems.AbstractSystem;

public class InputSystem extends AbstractSystem {

    private boolean[] keys;

    public InputSystem() {
        super("input", 900);
    }

    @Handler
    private void onKeyEvent(KeyEvent event) {
        keys[event.keycode] = event.pressed;
    }

    @Override
    protected void init() {
        keys = new boolean[1024];
    }

    @Override
    protected void destroy() {

    }
}
