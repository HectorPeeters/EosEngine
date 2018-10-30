package com.hector.engine.entity;

import com.hector.engine.event.events.AddEntityComponentEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.event.events.RemoveEntityComponentEvent;

import java.util.ArrayList;
import java.util.List;

public class Entity {

    private static int ID_COUNTER = 0;

    private int id;
    public String name;

    private List<AbstractEntityComponent> components;

    public Entity() {
        this.id = ID_COUNTER++;
        this.name = "";

        this.components = new ArrayList<>();
    }

    public void addComponent(AbstractEntityComponent component) {
        components.add(component);
        component.setParent(this);

        EventSystem.publish(new AddEntityComponentEvent(this, component));
    }

    public void removeComponent(AbstractEntityComponent component) {
        EventSystem.publish(new RemoveEntityComponentEvent(this, component));

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
