package com.hector.engine.scripting;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.event.Handler;
import com.hector.engine.scripting.components.AbstractScriptComponent;
import com.hector.engine.systems.AbstractSystem;

import java.util.ArrayList;
import java.util.List;

public class ScriptSystem extends AbstractSystem {

    private List<AbstractScriptComponent> scriptComponents = new ArrayList<>();

    public ScriptSystem() {
        super("script", 3000);
    }

    @Override
    public void update(float delta) {
        for (AbstractScriptComponent component : scriptComponents)
            component.updateScript(delta);
    }

    @Handler
    private void onScriptComponentAdded(AddEntityComponentEvent event) {

        for (AbstractEntityComponent comp : event.components) {
            if (comp instanceof AbstractScriptComponent)
                scriptComponents.add((AbstractScriptComponent) comp);
        }
    }

    @Handler
    private void onScriptComponentRemoved(RemoveEntityComponentEvent event) {
        for (AbstractEntityComponent comp : event.components) {
            if (comp instanceof AbstractScriptComponent)
                scriptComponents.remove(comp);
        }
    }

    @Override
    protected void init() {

    }

    @Override
    protected void reset() {
        scriptComponents.clear();
    }

    @Override
    protected void destroy() {

    }
}
