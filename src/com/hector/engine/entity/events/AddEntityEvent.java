package com.hector.engine.entity.events;

import com.hector.engine.entity.Entity;

public class AddEntityEvent {

    public final Entity entity;

    public AddEntityEvent(Entity entity) {
        this.entity = entity;
    }
}
