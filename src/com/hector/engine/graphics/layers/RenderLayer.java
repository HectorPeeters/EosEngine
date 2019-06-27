package com.hector.engine.graphics.layers;

public abstract class RenderLayer {

    public abstract void init();

    public abstract void update(float delta);
    public abstract void render();

    public abstract void onEvent(LayerInputEvent event);

    public abstract void destroy();
}
