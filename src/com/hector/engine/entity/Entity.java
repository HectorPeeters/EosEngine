package com.hector.engine.entity;

import com.hector.engine.maths.Matrix3f;
import com.hector.engine.maths.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private static int ID_COUNTER = 0;

    private final int id;
    public String name;

    private List<AbstractEntityComponent> components;


    //region Transform
    public Vector2f position = new Vector2f(0, 0);
    public Vector2f scale = new Vector2f(1, 1);
    public float rotation = 0f;
    //endregion


    public Entity(Vector2f position) {
        this();
        this.position = position;
    }

    public Entity(Vector2f position, Vector2f scale) {
        this();
        this.position = position;
        this.scale = scale;
    }

    public Entity(Vector2f position, Vector2f scale, float rotation) {
        this();
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public Entity() {
        this.id = ID_COUNTER++;
        this.name = "Entity " + id;

        this.components = new ArrayList<>();
    }

    public Entity addComponent(AbstractEntityComponent component) {
        components.add(component);
        component.setParent(this);

        return this;
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

    public Iterable<? extends AbstractEntityComponent> getComponents() {
        return components;
    }

    public Matrix3f getTransformationMatrix() {
        return new Matrix3f().initTransformation(position, scale, rotation);
    }
}
