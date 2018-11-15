package com.hector.engine.scripting.components;

import com.hector.engine.entity.Entity;
import com.hector.engine.entity.events.AddEntityEvent;
import com.hector.engine.event.EventSystem;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class GroovyScript {

    protected Entity parent;

    public void init() {
    }

    public void update(float delta) {
    }

    protected final void instantiate(Entity entity) {
        EventSystem.publishImmediate(new AddEntityEvent(entity));
    }

    protected final Entity findEntity(String name) {
        throw new NotImplementedException();
    }

}
