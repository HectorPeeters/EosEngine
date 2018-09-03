package com.hector.engine;

import com.hector.engine.event.EventSystem;
import com.hector.engine.logging.Logger;
import com.hector.engine.process.AddProcessEvent;
import com.hector.engine.process.DelayProcess;
import com.hector.engine.process.ProcessSystem;
import com.hector.engine.profiling.Profiling;
import com.hector.engine.systems.SystemManager;
import com.hector.engine.utils.UpdateTimer;
import com.hector.engine.xml.XMLConfigFile;

public class Engine {

    private SystemManager manager;

    private boolean running = true;

    public Engine() {
        Logger.init("assets/config/logging.xml");

        Logger.info("Engine", "Starting engine");

        manager = new SystemManager();
        manager.addSystem(EventSystem.class);
        manager.addSystem(ProcessSystem.class);
        manager.initSystems();

        XMLConfigFile engineConfig = new XMLConfigFile("assets/config/engine.xml");
        engineConfig.load();

        UpdateTimer timer = new UpdateTimer(engineConfig.getInt("target_fps"));

        while (running) {
            while (timer.shouldUpdateFPS())
                update((float) timer.getDelta());

            if (timer.shouldUpdateSecond()) {
                Logger.debug("Engine", "FPS: " + timer.getFrames() + ", UPS: " + timer.getUpdates());
                Profiling.printProfilingInfo();
                running = false;
            }

            render();
        }

        manager.destroySystems();
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
