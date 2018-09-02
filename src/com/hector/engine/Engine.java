package com.hector.engine;

import com.hector.engine.logging.Logger;
import com.hector.engine.systems.SystemManager;

public class Engine {

    private SystemManager manager;

    public Engine() {
        Logger.init("assets/config/logging.xml");

        Logger.info("Engine", "Starting engine");

        manager = new SystemManager();

        manager.initSystems();

        UpdateTimer timer = new UpdateTimer(60);

        while (true) {
            boolean shouldUpdate = timer.shouldUpdateFPS();
            float delta = (float) timer.getDelta();

            if (shouldUpdate) {
                update(delta);
            }

            if (timer.shouldUpdateSecond())
                System.out.println("FPS: " + timer.getFrames() + ", UPS: " + timer.getUpdates() + ", delta: " + delta);

            render();
        }
    }

    private void update(float delta) {
        manager.updateSystems(delta);
    }

    private void render() {
        manager.renderSystems();
    }

    public static void main(String[] args) {
        new Engine();
    }

}
