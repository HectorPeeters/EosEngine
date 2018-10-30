package com.hector.engine.event.events;

import com.hector.engine.entity.Entity;

public class RemoveEntityEvent {

    public final Entity entity;

    public RemoveEntityEvent(Entity entity) {
        this.entity = entity;
    }
}
