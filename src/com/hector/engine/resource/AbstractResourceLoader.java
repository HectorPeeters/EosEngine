package com.hector.engine.resource;

import com.hector.engine.resource.resources.StreamResource;
import com.hector.engine.resource.resources.TextResource;

public abstract class AbstractResourceLoader {

    public abstract TextResource getTextResource(String path);

    public abstract String[] listFiles(String path);

    public abstract boolean doesFileExist(String path);

    public abstract StreamResource getStreamResource(String path);
}
