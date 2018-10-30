package com.hector.engine.graphics;

import com.hector.engine.systems.AbstractSystem;

public class GraphicsSystem extends AbstractSystem {

    private Display display;

    public GraphicsSystem() {
        super("graphics", 1500);
    }

    @Override
    protected void init() {
        display = new Display();

        int displayWidth = config.getInt("width");
        int displayHeight = config.getInt("height");

        display.create(displayWidth, displayHeight);
    }

    @Override
    public void postRender() {
        display.update();
    }

    @Override
    protected void destroy() {
        display.destroy();
    }
}
