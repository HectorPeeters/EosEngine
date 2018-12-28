package com.hector.engine.graphics.components;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextureResource;

public class SpriteComponent extends AbstractEntityComponent {

    public int textureId;
    public int sortingLayer = 0;

    private final String path;

    public SpriteComponent(String path) {
        this.path = path;
    }

    public SpriteComponent(String path, int sortingLayer) {
        this.path = path;
        this.sortingLayer = sortingLayer;
    }

    @Override
    public void init() {
        TextureResource resource = ResourceManager.getResource(path);
        if (resource == null)
            return;

        textureId = resource.getResource().getId();
    }

}
