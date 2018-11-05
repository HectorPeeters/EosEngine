package com.hector.engine.resource.resources;

public abstract class AbstractResource {

    public String path;

    public boolean shouldReload;

    public AbstractResource(String path, boolean shouldReload) {
        this.path = path;
        this.shouldReload = shouldReload;
    }

}
