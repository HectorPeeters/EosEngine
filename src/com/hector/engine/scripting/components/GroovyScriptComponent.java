package com.hector.engine.scripting.components;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextResource;
import groovy.lang.GroovyClassLoader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class GroovyScriptComponent extends AbstractScriptComponent {

    private static Map<String, Class> scriptCache = new HashMap<>();

    private static Map<String, Object> variables = new HashMap<>();

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

            if (resource == null)
                return;

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

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            for (Field f : script.getClass().getDeclaredFields()) {
                if (f.getName().equals(entry.getKey())) {
                    try {
                        f.setAccessible(true);
                        f.set(script, entry.getValue());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void set(String name, Object object) {
        if (script == null) {
            variables.put(name, object);
            return;
        }

        for (Field f : script.getClass().getDeclaredFields()) {
            if (f.getName().equals(name)) {
                try {
                    f.setAccessible(true);
                    f.set(script, object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
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
