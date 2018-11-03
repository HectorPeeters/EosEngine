package com.hector.engine.entity;

import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    private static List<Entity> entities = new ArrayList<>();

    public static void addEntity(Entity entity) {
        entities.add(entity);
    }

    public static void removeEntity(Entity entity) {
        entities.remove(entity);
    }

}
