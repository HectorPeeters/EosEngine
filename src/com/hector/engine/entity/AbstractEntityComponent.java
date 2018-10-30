package com.hector.engine.entity;

public abstract class AbstractEntityComponent {

    private Entity parent;

    public Entity getParent() {
        return parent;
    }

    void setParent(Entity parent) {
        this.parent = parent;
    }
}
