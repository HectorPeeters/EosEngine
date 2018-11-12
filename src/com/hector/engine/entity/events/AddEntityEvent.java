package com.hector.engine.entity.events;

import com.hector.engine.entity.Entity;

import java.util.Collections;
import java.util.List;

public class AddEntityEvent {

    public final List<Entity> entity;

    public AddEntityEvent(Entity entity) {
        this.entity = Collections.singletonList(entity);
    }

    public AddEntityEvent(List<Entity> entities) {
        this.entity = entities;
    }
}
