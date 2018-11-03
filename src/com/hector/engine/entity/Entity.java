package com.hector.engine.entity;

import com.hector.engine.maths.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private static int ID_COUNTER = 0;

    private int id;
    public String name;

    public Vector2f position = new Vector2f(0, 0);
    public Vector2f scale = new Vector2f(1, 1);
    public float rotation = 0f;

    private List<AbstractEntityComponent> components;

    public Entity(Vector2f position) {
        super();
        this.position = position;
    }

    public Entity() {
        this.id = ID_COUNTER++;
        this.name = "";

        this.components = new ArrayList<>();
    }

    public void addComponent(AbstractEntityComponent component) {
        components.add(component);
        component.setParent(this);
    }

    public void removeComponent(AbstractEntityComponent component) {
        components.remove(component);
        component.setParent(null);
    }

    public <T extends AbstractEntityComponent> T getComponent(Class<? extends AbstractEntityComponent> componentClass) {
        for (AbstractEntityComponent component : components)
            if (component.getClass() == componentClass)
                return (T) component;

        return null;
    }

    public boolean hasComponent(Class<? extends AbstractEntityComponent> componentClass) {
        for (AbstractEntityComponent component : components)
            if (component.getClass() == componentClass)
                return true;

        return false;
    }
}
