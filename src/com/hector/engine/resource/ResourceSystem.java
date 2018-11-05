package com.hector.engine.resource;

import com.hector.engine.Engine;
import com.hector.engine.logging.Logger;
import com.hector.engine.resource.resources.StreamResource;
import com.hector.engine.resource.resources.TextResource;
import com.hector.engine.systems.AbstractSystem;

public class ResourceSystem extends AbstractSystem {

    private static AbstractResourceLoader resourceLoader;

    public ResourceSystem() {
        super("resource", 50);
    }

    public static TextResource getTextResource(String path) {
        return resourceLoader.getTextResource(path);
    }

    public static StreamResource getStreamResource(String path) {
        return resourceLoader.getStreamResource(path);
    }

    public static boolean doesFileExist(String path) {
        if (resourceLoader == null) //TODO: quick dirty fix!!!
            return false;
        return resourceLoader.doesFileExist(path);
    }

    @Override
    protected void init() {
        resourceLoader = Engine.DEV_BUILD ? new FileSystemResourceLoader() : new ZipResourceLoader();

        Logger.info("Resource", "Initialized resource system with " + resourceLoader.getClass().getSimpleName());
    }

    @Override
    protected void destroy() {

    }
}
