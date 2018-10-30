package com.hector.engine.graphics;

import com.hector.engine.entity.components.SpriteComponent;
import com.hector.engine.event.Handler;
import com.hector.engine.event.events.AddEntityComponentEvent;
import com.hector.engine.event.events.RemoveEntityComponentEvent;
import com.hector.engine.event.events.RemoveEntityEvent;
import com.hector.engine.systems.AbstractSystem;

public class GraphicsSystem extends AbstractSystem {

    private Display display;

    private Shader shader;

    public GraphicsSystem() {
        super("graphics", 1500);
    }

    @Override
    protected void init() {
        display = new Display();

        int displayWidth = config.getInt("width");
        int displayHeight = config.getInt("height");
        display.create(displayWidth, displayHeight);

        shader = new Shader("basic");
    }

    @Handler
    private void onComponentAddEvent(AddEntityComponentEvent event) {
        if (!(event.component instanceof SpriteComponent))
            return;
    }

    @Handler
    private void onComponentRemoveEvent(RemoveEntityComponentEvent event) {
        if (!(event.component instanceof SpriteComponent))
            return;
    }

    @Handler
    private void onEntityRemoveEvent(RemoveEntityEvent event) {
        SpriteComponent component = event.entity.getComponent(SpriteComponent.class);
        if (component == null)
            return;


    }

    @Override
    public void postRender() {
        display.update();
    }

    @Override
    protected void destroy() {
        shader.destroy();

        display.destroy();
    }
}
