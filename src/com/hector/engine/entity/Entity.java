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

    private List<EntityComponent> components;

    public Entity() {
        this.id = ID_COUNTER++;
        this.name = "";

        this.components = new ArrayList<>();
    }

    public void addComponent(EntityComponent component) {
        components.add(component);
        EventSystem.publish(new AddEntityComponentEvent(this, component));
    }

    public void removeComponent(EntityComponent component) {
        EventSystem.publish(new RemoveEntityComponentEvent(this, component));
        components.remove(component);
    }

    public <T extends EntityComponent> T getComponent(Class<? extends EntityComponent> componentClass) {
        for (EntityComponent component : components)
            if (component.getClass() == componentClass)
                return (T) component;

        return null;
    }

    public boolean hasComponent(Class<? extends EntityComponent> componentClass) {
        for (EntityComponent component : components)
            if (component.getClass() == componentClass)
                return true;

        return false;
    }
}
