package com.hector.engine.entity.components;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.maths.Vector2f;

public class TransformComponent extends AbstractEntityComponent {

    public Vector2f position;
    public Vector2f scale = new Vector2f(1, 1);
    public float rotation = 0f;

    public TransformComponent(Vector2f position) {
        this.position = position;
    }
}
