package com.hector.engine.systems;

import com.hector.engine.xml.XMLConfigFile;

import java.io.File;

public abstract class AbstractSystem {

    protected final String name;
    protected final int initPriority;
    protected final XMLConfigFile config;

    public AbstractSystem(String name, int initPriority) {
        this.name = name;
        this.initPriority = initPriority;

        String configFile = "assets/config/" + name.toLowerCase() + ".xml";
        if (new File(configFile).exists())
            this.config = new XMLConfigFile(configFile);
        else
            this.config = null;
    }

    public void initModule() {
        if (config != null)
            config.load();
        init();
    }

    protected abstract void init();


    public void preUpdate(float delta) {}

    public void update(float delta) {}

    public void postUpdate(float delta) {}


    public void preRender() {}

    public void render() {}

    public void postRender() {}


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
