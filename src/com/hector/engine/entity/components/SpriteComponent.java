package com.hector.engine.entity.components;

import com.hector.engine.entity.AbstractEntityComponent;

public class SpriteComponent extends AbstractEntityComponent {

    public int textureId;

    public SpriteComponent(int textureId) {
        this.textureId = textureId;
    }
}
