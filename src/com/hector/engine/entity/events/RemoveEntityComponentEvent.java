package com.hector.engine.entity.events;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.Entity;

import java.util.Collections;
import java.util.List;

public class RemoveEntityComponentEvent {

    public final List<AbstractEntityComponent> components;

    public RemoveEntityComponentEvent(AbstractEntityComponent component) {
        this.components = Collections.singletonList(component);
    }

    public RemoveEntityComponentEvent(List<AbstractEntityComponent> components) {
        this.components = components;
    }
}
