package com.hector.engine.resource.resources;

import com.hector.engine.resource.AbstractResourceLoader;

public abstract class AbstractResource<T> {

    protected T resource;
    protected String path;

    public AbstractResource(String path) {
        this.path = path;
    }

    public abstract boolean load(AbstractResourceLoader resourceLoader);

    public T getResource() {
        return resource;
    }

    public String getPath() {
        return path;
    }
}
