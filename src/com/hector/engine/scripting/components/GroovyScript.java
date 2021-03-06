package com.hector.engine.scripting.components;

import com.hector.engine.entity.Entity;
import com.hector.engine.entity.events.AddEntityEvent;
import com.hector.engine.event.EventSystem;

public abstract class GroovyScript {

    protected Entity parent;

    public void init() {
    }

    public void update(float delta) {
    }

    protected final void instantiate(Entity entity) {
        EventSystem.publish(new AddEntityEvent(entity));
    }

    protected final Entity findEntity(String name) {
        try {
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
