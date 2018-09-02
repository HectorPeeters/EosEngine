package com.hector.engine;

import com.hector.engine.event.EventSystem;
import com.hector.engine.logging.Logger;
import com.hector.engine.process.DelayProcess;
import com.hector.engine.systems.SystemManager;
import com.hector.engine.process.ProcessSystem;
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

        ProcessSystem processSystem = new ProcessSystem();
        DelayProcess process1 = new DelayProcess(3000);
        process1.attachChild(new DelayProcess(2000));
        processSystem.attachProcess(process1);

        while (running) {
            boolean shouldUpdate = timer.shouldUpdateFPS();
            float delta = (float) timer.getDelta();


            if (shouldUpdate) {
                processSystem.updateProcesses(delta);
                update(delta);
            }

            if (timer.shouldUpdateSecond())
                System.out.println("FPS: " + timer.getFrames() + ", UPS: " + timer.getUpdates() + ", delta: " + delta);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
