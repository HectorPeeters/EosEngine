package com.hector.engine.graphics.components;

import com.hector.engine.entity.AbstractEntityComponent;

public class SpriteComponent extends AbstractEntityComponent {

    public int textureId;
    public int sortingLayer = 0;

    public SpriteComponent(int textureId) {
        this.textureId = textureId;
    }

    public SpriteComponent(int textureId, int sortingLayer) {
        this.textureId = textureId;
        this.sortingLayer = sortingLayer;
    }
}
