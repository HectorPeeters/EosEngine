package com.hector.engine.graphics.layers;

public abstract class AbstractRenderLayer {

    public abstract void init();

    public void preUpdate(float delta) {}
    public abstract void update(float delta);
    public abstract void render();

    public abstract void onEvent(LayerInputEvent event);

    public abstract void destroy();
}
