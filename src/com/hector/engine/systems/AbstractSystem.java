package com.hector.engine.systems;

public abstract class AbstractSystem {

    protected final String name;
    protected final int initPriority;

    public AbstractSystem(String name, int initPriority) {
        this.name = name;
        this.initPriority = initPriority;
    }

    public abstract void init();

    public abstract void update(float delta);

    public abstract void render();

    public abstract void destroy();

    public String getName() {
        return name;
    }

    public int getInitPriority() {
        return initPriority;
    }

    @Override
    public String toString() {
        return "System (" + name + ")";
    }
}
