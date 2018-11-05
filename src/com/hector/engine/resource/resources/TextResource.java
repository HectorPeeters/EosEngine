package com.hector.engine.resource.resources;

import com.hector.engine.resource.AbstractResourceLoader;

public class TextResource extends AbstractResource<String> {

    public TextResource(String path) {
        super(path);
    }

    @Override
    public boolean load(AbstractResourceLoader resourceLoader) {
        resource = resourceLoader.loadText(path);

        if (resource == null)
            return false;

        return true;
    }
}
