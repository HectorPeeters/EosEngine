package com.hector.engine.resource.resources;

import com.hector.engine.graphics.Model;
import com.hector.engine.resource.AbstractResourceLoader;

public class ModelResource extends AbstractResource<Model> {

    public ModelResource(String path) {
        super(path);
    }

    @Override
    public boolean load(AbstractResourceLoader resourceLoader) {
        return false;
    }
}
