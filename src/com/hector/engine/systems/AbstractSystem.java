package com.hector.engine.systems;

import com.hector.engine.resource.ResourceManager;
import com.hector.engine.xml.XMLConfigFile;

public abstract class AbstractSystem {

    protected final String name;
    protected final int initPriority;
    protected XMLConfigFile config;

    public AbstractSystem(String name, int initPriority) {
        this.name = name;
        this.initPriority = initPriority;
    }

    public void initModule() {
        String configFile = "config/" + name.toLowerCase() + ".xml";

        if (ResourceManager.doesResourceExist(configFile)) {
            this.config = new XMLConfigFile(configFile);
            this.config.load();
        }

        init();
    }

    protected abstract void init();

    public void preUpdate(float delta) {
    }

    public void update(float delta) {
    }

    public void postUpdate(float delta) {
    }


    public void preRender() {
    }

    public void render() {
    }

    public void postRender() {
    }

    protected abstract void reset();
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
