package com.hector.engine;

import com.hector.engine.audio.AudioSystem;
import com.hector.engine.entity.EntitySystem;
import com.hector.engine.event.EventSystem;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.GraphicsSystem;
import com.hector.engine.input.InputSystem;
import com.hector.engine.logging.Logger;
import com.hector.engine.physics.PhysicsSystem;
import com.hector.engine.process.ProcessSystem;
import com.hector.engine.resource.ResourceBuilder;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.scene.SceneSystem;
import com.hector.engine.scripting.ScriptSystem;
import com.hector.engine.systems.SystemManager;
import com.hector.engine.utils.UpdateTimer;
import com.hector.engine.xml.XMLConfigFile;

public class Engine {

    public static final boolean DEV_BUILD = true;

    private SystemManager manager;

    private boolean running = true;

    private static UpdateTimer timer;

    public static float getDelta() {
        return (float) timer.getDelta();
    }

    public Engine() {
        init();

        while (running) {
            while (timer.shouldUpdateFPS())
                update((float) timer.getDelta());

//            if (timer.shouldUpdateSecond()) {
//                Logger.debug("Engine", "FPS: " + timer.getFrames() + ", UPS: " + timer.getUpdates());
//                Profiling.printProfilingInfo();
//            }

            render();
        }

        manager.destroySystems();
    }

    private void init() {
        if (DEV_BUILD) {
            ResourceBuilder.makeResourceArchive();
        }

        Logger.init();

        long startTime = System.currentTimeMillis();
        NativesLoader.loadNatives();

        ResourceManager.init();

        manager = new SystemManager();
        manager.addSystem(EventSystem.class);
        manager.addSystem(ProcessSystem.class);
        manager.addSystem(GraphicsSystem.class);
        manager.addSystem(InputSystem.class);
        manager.addSystem(EntitySystem.class);
        manager.addSystem(ScriptSystem.class);
        manager.addSystem(PhysicsSystem.class);
        manager.addSystem(AudioSystem.class);
        manager.addSystem(SceneSystem.class);
        manager.initSystems();

        manager.subscribe();

        Logger.info("Engine", "Started engine");

        EventSystem.subscribe(this);

        XMLConfigFile engineConfig = new XMLConfigFile("config/engine.xml");
        engineConfig.load();

        timer = new UpdateTimer(engineConfig.getInt("target_fps"));

        Logger.info("Engine", "Engine initialization took " + (System.currentTimeMillis() - startTime) + "ms");
    }

    private void update(float delta) {
        manager.updateSystems(delta);
    }

    private void render() {
        manager.renderSystems();
    }

    @Handler
    private void onEngineStateEventReceived(EngineStateEvent event) {
        if (event.state == EngineStateEvent.EngineState.STOP) {
            running = false;
        } else {
            Logger.warn("Engine", "Engine state not handled");
        }
    }

    public static void main(String[] args) {
        new Engine();
    }

}
