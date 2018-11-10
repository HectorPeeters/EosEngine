package com.hector.engine.resource;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.resources.AbstractResource;
import com.hector.engine.resource.resources.TextResource;
import com.hector.engine.resource.resources.TextureResource;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ResourceManager {

    private static AbstractResourceLoader resourceLoader;

    private static Map<String, AbstractResource> loadedResources;
    private static Map<Class<? extends AbstractResource>, List<String>> availableResourceTypes;

    //TODO: FIX
    public static <T extends AbstractResource> T getResource(String path) {
        if (loadedResources.containsKey(path))
            return (T) loadedResources.get(path);


        Class<? extends AbstractResource> resourceClass = getResourceClass(path);
        if (resourceClass == null) {
            Logger.warn("Resource", "File " + path + " is not supported");
            return null;
        }

        AbstractResource<T> resource;
        try {
            //TODO: better way to get resource class constructor
            resource = resourceClass.getConstructor(String.class).newInstance(path);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            Logger.err("Resource", "Failed to get constructor for resource " + path + " with type " + resourceClass.getSimpleName());
            return null;
        }

        resource.load(resourceLoader);

        Logger.debug("Resource", "Loaded resource " + path);

        loadedResources.put(path, resource);

        return (T) resource;
    }

    //TODO: optimize?
    private static Class<? extends AbstractResource> getResourceClass(String path) {
        for (Map.Entry<Class<? extends AbstractResource>, List<String>> entry : availableResourceTypes.entrySet())
            for (String s : entry.getValue())
                if (path.endsWith(s))
                    return entry.getKey();

        return null;
    }

    public static boolean doesFileExist(String path) {
        return resourceLoader.doesFileExist(path);
    }

    public static void init() {
        resourceLoader = new ZipResourceLoader();

        loadedResources = new HashMap<>();
        availableResourceTypes = new HashMap<>();

        availableResourceTypes.put(TextResource.class, new ArrayList<String>() {{
            add(".vert");
            add(".frag");
            add(".txt");
            add(".xml");
            add(".lua");
            add(".groovy");
        }});

        availableResourceTypes.put(TextureResource.class, new ArrayList<String>(){{
            add(".png");
        }});

        Logger.info("Resource", "Initialized resource system with " + resourceLoader.getClass().getSimpleName());
    }
}