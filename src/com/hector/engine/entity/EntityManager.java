package com.hector.engine.entity;

import com.hector.engine.event.events.AddEntityEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.event.events.RemoveEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    private static List<Entity> entities = new ArrayList<>();

    public static void addEntity(Entity entity) {
        entities.add(entity);
        EventSystem.publish(new AddEntityEvent(entity));
    }

    public static void removeEntity(Entity entity) {
        EventSystem.publish(new RemoveEntityEvent(entity));
        entities.remove(entity);
    }

}
