package com.hector.engine.ecs;

import com.hector.engine.event.AddEntityEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.event.RemoveEntityEvent;
import com.hector.engine.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ECS {

    private static int entityIdCounter = 0;

    private List<EntityListener> entityListeners = new ArrayList<>();

    private List<AbstractEntitySystem> systems = new ArrayList<>();

    private Map<Integer, List<IEntityComponent>> entities = new HashMap<>();

    public int addEntity(int id) {
        if (entities.containsKey(id)) {
            Logger.err("ECS", "Trying to add entity which already exists");
            return -1;
        }

        entities.put(id, new ArrayList<>());

        EventSystem.publish(new AddEntityEvent(id));

        for(EntityListener el : entityListeners)
            el.onEntityCreate(id);

        return id;
    }

    public int addEntity() {
        int id = entityIdCounter++;

        return addEntity(id);
    }

    public List<Integer> getEntities() {
        return new ArrayList<>(entities.keySet());
    }

    public void removeEntity(int entityId) {
        EventSystem.publish(new RemoveEntityEvent(entityId));

        entities.remove(entityId);

        for(EntityListener el : entityListeners)
            el.onEntityRemove(entityId);
    }


    public void addComponent(int entityId, IEntityComponent component) {
        if (!entities.containsKey(entityId)) {
            Logger.warn("ECS", "Entity: " + entityId + " does not exist");
            return;
        }

        entities.get(entityId).add(component);

        for(EntityListener el : entityListeners)
            el.onComponentAdd(entityId, component);
    }

    public List<IEntityComponent> getComponents(int entityId) {
        return entities.get(entityId);
    }

    public void removeComponent(int entityId, IEntityComponent component) {
        if (!entities.containsKey(entityId)) {
            Logger.warn("ECS", "Entity: " + entityId + " does not exist");
            return;
        }

        entities.get(entityId).remove(component);

        for(EntityListener el : entityListeners)
            el.onComponentRemove(entityId, component);
    }

    public <T extends IEntityComponent> T getComponent(int entityId, Class<T> component) {
        for (IEntityComponent comp : entities.get(entityId))
            if (comp.getClass() == component)
                return (T) comp;

        return null;
    }


    public void updateEntities(float delta) {
        processEntities((entity, system) -> system.update(entity, this, delta));
    }

    public void renderEntities() {
        processEntities((entity, system) -> system.render(entity, this));
    }

    private void processEntities(IEntityAction action) {
        //TODO: optimize
        for (AbstractEntitySystem system : systems) {

            for (Map.Entry<Integer, List<IEntityComponent>> entity : entities.entrySet()) {
                boolean update = true;

                for (Class<? extends IEntityComponent> comp : system.getComponentClasses()) {
                    if (getComponent(entity.getKey(), comp) == null) {
                        update = false;
                        break;
                    }
                }

                if (update)
                    action.onAction(entity.getKey(), system);
            }
        }
    }

    public void addSystem(AbstractEntitySystem system) {
        systems.add(system);
    }

    private interface IEntityAction {
        void onAction(int entity, AbstractEntitySystem system);
    }

    public void addListener(EntityListener listener) {
        entityListeners.add(listener);
    }

    public void removeListener(EntityListener listener) {
        entityListeners.remove(listener);
    }
}
