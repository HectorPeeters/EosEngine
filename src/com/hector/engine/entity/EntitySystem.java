package com.hector.engine.entity;

import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.AddEntityEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.event.Handler;
import com.hector.engine.systems.AbstractSystem;

import java.util.ArrayList;
import java.util.List;

public class EntitySystem extends AbstractSystem {

    private List<Entity> entities = new ArrayList<>();

    public EntitySystem() {
        super("entity", 2000);
    }

    @Handler
    private void onEntityAddEvent(AddEntityEvent event) {
        for(Entity e : event.entity) {
            entities.add(e);

            e.init();

            //TODO: batch this in one event
            for (AbstractEntityComponent component : e.getComponents())
                EventSystem.publish(new AddEntityComponentEvent(e, component));
        }
    }

    @Handler
    private void onEntityRemoveEvent(RemoveEntityEvent event) {
        entities.remove(event.entity);

        for (AbstractEntityComponent component : event.entity.getComponents())
            EventSystem.publish(new RemoveEntityComponentEvent(event.entity, component));
    }

    @Override
    protected void init() {

    }

    @Override
    protected void destroy() {

    }
}
