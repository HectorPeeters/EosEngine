package com.hector.engine.scripting.components;

import com.hector.engine.entity.Entity;
import com.hector.engine.entity.events.AddEntityEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.scene.events.SceneRequestEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public abstract class GroovyScript {

    protected Entity parent;

    public void init() {
    }

    public void update(float delta) {
    }

    protected final void instantiate(Entity entity) {
        EventSystem.publish(new AddEntityEvent(entity));
    }

    protected final Entity findEntity(String name) {
        throw new NotImplementedException();
    }

    protected final void enterScene(String sceneName) {
        EventSystem.publish(new SceneRequestEvent(sceneName));
    }

}
