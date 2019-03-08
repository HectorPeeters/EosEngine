package com.hector.engine.graphics.components;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextureResource;

public class TextureComponent extends AbstractEntityComponent {

    public int textureId;
    public int sortingLayer = 0;

    private final String path;

    public TextureComponent(String path) {
        this.path = path;
    }

    public TextureComponent(String path, int sortingLayer) {
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
