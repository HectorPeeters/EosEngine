package com.hector.engine.scene;

import com.hector.engine.entity.Entity;

import java.util.List;

public class Scene {

    public String name;

    private List<Entity> entities;

    public String getName() {
        return name;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
