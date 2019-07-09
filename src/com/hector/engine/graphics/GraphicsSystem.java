package com.hector.engine.graphics;

import com.hector.engine.graphics.layers.*;
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
        int samples = config.getInt("samples");
        display.create(width, height, samples);

        layerStack = new LayerStack();
        Render2DLayer render2DLayer = new Render2DLayer(width, height);

        DebugLayer debugLayer = new DebugLayer(display.getId());
        debugLayer.addWindow(new TestWindow());
        debugLayer.addWindow(new LogWindow());
        debugLayer.addWindow(new ResourceWindow());

        layerStack.addLayer(render2DLayer);
        layerStack.addOverlayLayer(debugLayer);
        layerStack.init();
    }

    @Override
    public void preUpdate(float delta) {
        layerStack.preUpdate(delta);
    }

    @Override
    public void update(float delta) {
        layerStack.update(delta);

        display.pollEvents();
    }

    @Override
    public void render() {
        layerStack.render();

        display.update();
    }

    @Override
    protected void destroy() {
//        display.destroy();
        layerStack.destroy();
    }
}
