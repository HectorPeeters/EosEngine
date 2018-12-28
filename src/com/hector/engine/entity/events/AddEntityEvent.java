package com.hector.engine.entity.events;

import com.hector.engine.entity.Entity;

import java.util.Collections;
import java.util.List;

public class AddEntityEvent {

    public final List<Entity> entities;

    public AddEntityEvent(Entity entity) {
        this.entities = Collections.singletonList(entity);
    }

    public AddEntityEvent(List<Entity> entities) {
        this.entities = entities;
    }
}
