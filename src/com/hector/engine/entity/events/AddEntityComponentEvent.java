package com.hector.engine.entity.events;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.Entity;

public class AddEntityComponentEvent {

    public final Entity entity;
    public final AbstractEntityComponent component;

    public AddEntityComponentEvent(Entity entity, AbstractEntityComponent component) {
        this.entity = entity;
        this.component = component;
    }
}
