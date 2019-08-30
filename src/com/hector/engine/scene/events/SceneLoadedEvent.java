package com.hector.engine.scene.events;

import com.hector.engine.scene.Scene;

public class SceneLoadedEvent {

    public final Scene scene;

    public SceneLoadedEvent(Scene scene) {
        this.scene = scene;
    }
}
