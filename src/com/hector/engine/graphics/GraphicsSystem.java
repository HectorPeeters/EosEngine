package com.hector.engine.graphics;

import com.hector.engine.Engine;
import com.hector.engine.EngineStateEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.graphics.layers.DebugLayer;
import com.hector.engine.graphics.layers.LayerStack;
import com.hector.engine.graphics.layers.RaytraceLayer;
import com.hector.engine.graphics.layers.Render2DLayer;
import com.hector.engine.logging.Logger;
import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.opengl.GL11;

public class GraphicsSystem extends AbstractSystem {

    private Display display;
    private LayerStack layerStack;

    public GraphicsSystem() {
        super("graphics", 1500);
    }

    @Override
    protected void init() {
        display = new Display();
        int width = getConfig().getInt("width");
        int height = getConfig().getInt("height");
        int samples = getConfig().getInt("samples");
        display.create(width, height, samples);

        layerStack = new LayerStack();
        Render2DLayer render2DLayer = new Render2DLayer(width, height);

        DebugLayer debugLayer = new DebugLayer(display.getId());
//        debugLayer.addWindow(new TestWindow());
//        debugLayer.addWindow(new LogWindow());
//        debugLayer.addWindow(new ResourceWindow());
//        debugLayer.addWindow(new ProfileWindow());

//        layerStack.addLayer(render2DLayer);
//        layerStack.addLayer(new Render3DLayer(width, height));
        layerStack.addLayer(new RaytraceLayer());
        layerStack.addOverlayLayer(debugLayer);
        layerStack.init();

    }

    @Override
    public void preUpdate(float delta) {
        layerStack.preUpdate(delta);
    }

    @Override
    public void update(float delta) {
        if (Engine.DEV_BUILD) {
            int error = GL11.glGetError();

            if (error != 0) {
                Logger.fatal("Graphics", "OpenGL error: " + error + " [" + ErrorCodes.getErrorString(error) + "]");
                EventSystem.publishImmediate(new EngineStateEvent(EngineStateEvent.EngineState.STOP));
                return;
            }
        }

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
