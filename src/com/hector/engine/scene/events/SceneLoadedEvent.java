package com.hector.engine.scene.events;

import com.hector.engine.scene.Scene;

public class SceneLoadedEvent {

    public Scene scene;

    public SceneLoadedEvent(Scene scene) {
        this.scene = scene;
    }
}
