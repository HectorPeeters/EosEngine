package com.hector.engine.scripting.components;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextResource;
import groovy.lang.GroovyClassLoader;

import java.util.HashMap;
import java.util.Map;

public class GroovyScriptComponent extends AbstractScriptComponent {

    private static Map<String, Class> scriptCache = new HashMap<>();

    private static GroovyClassLoader gcl = new GroovyClassLoader(new CustomGroovyClassLoader());

    private String path;

    private GroovyScript script;

    public GroovyScriptComponent(String path) {
        this.path = path;
    }

    @Override
    public void init() {
        Class clazz;

        if (scriptCache.containsKey(path)) {
            clazz = scriptCache.get(path);
        } else {
            TextResource resource = ResourceManager.getResource(path);

            clazz = gcl.parseClass(resource.getResource());

            scriptCache.put(path, clazz);
        }

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
