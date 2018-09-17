package com.hector.engine.ecs;

public abstract class AbstractEntitySystem {

    private Class<? extends IEntityComponent>[] componentClasses;

    public AbstractEntitySystem(Class<? extends IEntityComponent>... componentClasses) {
        this.componentClasses = componentClasses;
    }

    public abstract void update(int entity, ECS manager, float delta);

    public abstract void render(int entity, ECS manager);

    public Class<? extends IEntityComponent>[] getComponentClasses() {
        return componentClasses;
    }

}
