package com.hector.engine.ecs;

public abstract class EntityListener {

    public void onEntityCreate(int id) {}

    public void onEntityRemove(int id) {}

    public void onComponentAdd(int id, IEntityComponent component) {}

    public void onComponentRemove(int id, IEntityComponent component) {}

}
