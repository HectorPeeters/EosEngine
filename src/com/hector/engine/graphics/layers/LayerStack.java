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
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).init();
        }
    }

    public void update(float delta) {
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).update(delta);
        }
    }

    public void render() {
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).render();
        }
    }

    public void onEvent(LayerInputEvent event) {
        for (int i = layers.size() - 1; i >= 0; i--) {
            layers.get(i).onEvent(event);
        }
    }

    public void destroy() {
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).destroy();
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
