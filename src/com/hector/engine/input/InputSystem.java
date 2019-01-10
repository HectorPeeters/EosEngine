package com.hector.engine.input;

import com.hector.engine.event.Handler;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.systems.AbstractSystem;

public class InputSystem extends AbstractSystem {

    private static final int KEY_COUNT = 1024;

    private static boolean[] keysDown;

    public InputSystem() {
        super("input", 900);
    }

    @Override
    protected void init() {
        keysDown = new boolean[KEY_COUNT];
    }

    @Handler
    private void onKeyEvent(KeyEvent event) {
        keysDown[event.keycode] = event.pressed;
    }

    @Override
    public void postUpdate(float delta) {

    }

    @Override
    protected void reset() {

    }

    @Override
    protected void destroy() {

    }

    public static boolean isKeyDown(int keyCode) {
        return keysDown[keyCode];
    }
}
