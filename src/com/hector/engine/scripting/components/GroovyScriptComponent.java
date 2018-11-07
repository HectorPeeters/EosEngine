package com.hector.engine.scripting.components;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextResource;
import groovy.lang.GroovyClassLoader;

public class GroovyScriptComponent extends AbstractScriptComponent {

    private static GroovyClassLoader gcl = new GroovyClassLoader();

    private String path;

    private GroovyScript script;

    public GroovyScriptComponent(String path) {
        this.path = path;
    }

    @Override
    public void init() {
        TextResource resource = ResourceManager.getResource(path);

        Class clazz = gcl.parseClass(resource.getResource());

        try {
            script = (GroovyScript) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            Logger.err("Scripting", "Failed to instantiate groovy script " + path);
            return;
        }

        script.parent = parent;
        script.init();
    }

    @Override
    public void updateScript(float delta) {
        if (script != null)
            script.update(delta);
    }
}
