package com.hector.engine.scene;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.logging.Logger;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextResource;
import com.hector.engine.scene.events.SceneLoadedEvent;
import com.hector.engine.systems.AbstractSystem;

public class SceneSystem extends AbstractSystem {

    public SceneSystem() {
        super("scene", 4000);
    }

    @Override
    protected void init() {
        String startSceneFile = "scenes/" + config.getString("start_scene");

        Scene scene = loadScene(startSceneFile);

        if (scene == null) {
            Logger.err("Scene", "Failed to load scene file: " + startSceneFile);
            return;
        }

        //Send scene
        EventSystem.publish(new SceneLoadedEvent(scene));
    }

    private Scene loadScene(String sceneFile) {
        TextResource textResource = ResourceManager.getResource(sceneFile);

        if (textResource == null)
            return null;

        String json = textResource.getResource();

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(AbstractEntityComponent.class, new ComponentDeserializer());
        Gson gson = builder.create();

        Scene scene = gson.fromJson(json, Scene.class);

        if (scene == null) {
            Logger.err("Scene", "Failed to load scene: " + sceneFile);
            return null;
        }

        Logger.info("Scene", "Loaded scene: " + scene.getName());

        return scene;
    }

    @Override
    protected void destroy() {
        //Delete all entities
    }

}
