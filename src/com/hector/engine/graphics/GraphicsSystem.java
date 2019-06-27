package com.hector.engine.graphics;

import com.hector.engine.graphics.layers.DebugLayer;
import com.hector.engine.graphics.layers.LayerStack;
import com.hector.engine.graphics.layers.Render2DLayer;
import com.hector.engine.systems.AbstractSystem;

public class GraphicsSystem extends AbstractSystem {

    private Display display;
    private LayerStack layerStack;

    public GraphicsSystem() {
        super("graphics", 1500);
    }

    @Override
    protected void init() {
        display = new Display();
        int width = config.getInt("width");
        int height = config.getInt("height");
        display.create(width, height);

        layerStack = new LayerStack();
        layerStack.addLayer(new Render2DLayer(width, height));
        layerStack.addOverlayLayer(new DebugLayer());
        layerStack.init();
    }

    @Override
    public void update(float delta) {
        layerStack.update(delta);
    }

    @Override
    public void render() {
        layerStack.render();

        display.update();
    }

    @Override
    protected void destroy() {
        display.destroy();
        layerStack.destroy();
    }
}
