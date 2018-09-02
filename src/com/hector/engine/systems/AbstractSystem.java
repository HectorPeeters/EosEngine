package com.hector.engine.systems;

import com.hector.engine.xml.XMLConfigFile;

public abstract class AbstractSystem {

    protected final String name;
    protected final int initPriority;
    protected final XMLConfigFile config;

    public AbstractSystem(String name, int initPriority) {
        this.name = name;
        this.initPriority = initPriority;
        this.config = new XMLConfigFile("assets/config/" + name.toLowerCase() + ".xml");
    }

    public void initModule() {
        config.load();
        init();
    }

    protected abstract void init();

    public void update(float delta) {}

    public void render() {}

    public void destroyModule() {
        destroy();
    }

    protected abstract void destroy();

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
