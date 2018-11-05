package com.hector.engine.entity;

public abstract class AbstractEntityComponent {

    protected Entity parent;

    public Entity getParent() {
        return parent;
    }

    public void init() {}

    void setParent(Entity parent) {
        this.parent = parent;
    }
}
