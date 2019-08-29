package com.hector.engine.entity;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private static int ID_COUNTER = 0;

    public String name;

    private List<AbstractEntityComponent> components;

    //region Transform
    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f scale = new Vector3f(1, 1, 1);
    private float rotation = 0f;
    //endregion


    public Entity(Vector3f position) {
        this();
        this.position = position;
    }

    public Entity(Vector3f position, Vector3f scale) {
        this();
        this.position = position;
        this.scale = scale;
    }

    public Entity(Vector3f position, Vector3f scale, float rotation) {
        this();
        this.position = position;
        this.scale = scale;
        this.rotation = rotation;
    }

    public Entity() {
        this.name = "Entity " + ID_COUNTER++;

        this.components = new ArrayList<>();
    }

    public void init() {
        for (AbstractEntityComponent component : components) {
            if (component == null)
                continue;

            component.setParent(this);
            component.init();
        }
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

    public Matrix4f getTransformationMatrix() {
        return new Matrix4f().translate(position).scale(scale).rotateX(rotation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

}
