package com.hector.engine.entity.events;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.Entity;

import java.util.Collections;
import java.util.List;

public class AddEntityComponentEvent {

    public final List<? extends AbstractEntityComponent> components;

    public AddEntityComponentEvent(AbstractEntityComponent component) {
        this.components = Collections.singletonList(component);
    }

    public AddEntityComponentEvent(List<? extends AbstractEntityComponent> components) {
        this.components = components;
    }
}
