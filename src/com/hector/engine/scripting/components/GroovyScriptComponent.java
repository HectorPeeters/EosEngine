package com.hector.engine.scripting.components;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextResource;
import groovy.lang.GroovyClassLoader;

public class GroovyScriptComponent extends AbstractScriptComponent {

    private static GroovyClassLoader gcl = new GroovyClassLoader(new CustomGroovyClassLoader());

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

    private static class CustomGroovyClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String s) throws ClassNotFoundException {
            return super.loadClass(s);
        }

        @Override
        protected Class<?> loadClass(String s, boolean b) throws ClassNotFoundException {
            return super.loadClass(s, b);
        }
    }
}
