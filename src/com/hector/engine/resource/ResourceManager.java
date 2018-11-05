package com.hector.engine.resource;

import com.hector.engine.Engine;
import com.hector.engine.logging.Logger;
import com.hector.engine.resource.resources.StreamResource;
import com.hector.engine.resource.resources.TextResource;

public final class ResourceManager {

    private static AbstractResourceLoader resourceLoader;

    public static TextResource getTextResource(String path) {
        return resourceLoader.getTextResource(path);
    }

    public static StreamResource getStreamResource(String path) {
        return resourceLoader.getStreamResource(path);
    }

    public static boolean doesFileExist(String path) {
        return resourceLoader.doesFileExist(path);
    }

    public static void init() {
        resourceLoader = Engine.DEV_BUILD ? new FileSystemResourceLoader() : new ZipResourceLoader();

        Logger.info("Resource", "Initialized resource system with " + resourceLoader.getClass().getSimpleName());
    }
}
