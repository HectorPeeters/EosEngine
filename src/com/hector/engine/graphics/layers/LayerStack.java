package com.hector.engine.graphics.layers;

import java.util.ArrayList;
import java.util.List;

public class LayerStack {

    //Last item is item furthest layer
    private List<RenderLayer> layers;

    private int overlayIndex = 0;

    public LayerStack() {
        layers = new ArrayList<>();
    }

    public void init() {
        for (RenderLayer layer : layers) {
            layer.init();
        }
    }

    public void preUpdate(float delta) {
        for (RenderLayer layer : layers) {
            layer.preUpdate(delta);
        }
    }

    public void update(float delta) {
        for (RenderLayer layer : layers) {
            layer.update(delta);
        }
    }

    public void render() {
        for (RenderLayer layer : layers) {
            layer.render();
        }
    }

    public void onEvent(LayerInputEvent event) {
        for (int i = layers.size() - 1; i >= 0; i--) {
            layers.get(i).onEvent(event);
        }
    }

    public void destroy() {
        for (RenderLayer layer : layers) {
            layer.destroy();
        }
    }

    public void addLayer(RenderLayer layer) {
        layers.add(layers.size() - overlayIndex, layer);
    }

    public void addOverlayLayer(RenderLayer layer) {
        layers.add(layer);
        overlayIndex++;
    }

}
