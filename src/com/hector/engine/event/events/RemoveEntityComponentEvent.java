package com.hector.engine.event.events;

import com.hector.engine.entity.Entity;
import com.hector.engine.entity.EntityComponent;

public class RemoveEntityComponentEvent {

    public final Entity entity;
    public final EntityComponent component;

    public RemoveEntityComponentEvent(Entity entity, EntityComponent component) {
        this.entity = entity;
        this.component = component;
    }
}
