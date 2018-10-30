package com.hector.engine.event.events;

import com.hector.engine.entity.Entity;
import com.hector.engine.entity.AbstractEntityComponent;

public class AddEntityComponentEvent {

    public final Entity entity;
    public final AbstractEntityComponent component;

    public AddEntityComponentEvent(Entity entity, AbstractEntityComponent component) {
        this.entity = entity;
        this.component = component;
    }
}
