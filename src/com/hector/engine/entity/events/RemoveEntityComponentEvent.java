package com.hector.engine.entity.events;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.Entity;

public class RemoveEntityComponentEvent {

    public final Entity entity;
    public final AbstractEntityComponent component;

    public RemoveEntityComponentEvent(Entity entity, AbstractEntityComponent component) {
        this.entity = entity;
        this.component = component;
    }
}
