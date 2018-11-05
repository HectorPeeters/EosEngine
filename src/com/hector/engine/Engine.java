package com.hector.engine;

import com.hector.engine.entity.Entity;
import com.hector.engine.entity.EntitySystem;
import com.hector.engine.entity.events.AddEntityEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.GraphicsSystem;
import com.hector.engine.graphics.components.SpriteComponent;
import com.hector.engine.input.InputSystem;
import com.hector.engine.logging.Logger;
import com.hector.engine.maths.Vector2f;
import com.hector.engine.process.ProcessSystem;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.scripting.ScriptSystem;
import com.hector.engine.scripting.components.ScriptComponent;
import com.hector.engine.systems.SystemManager;
import com.hector.engine.utils.UpdateTimer;
import com.hector.engine.xml.XMLConfigFile;

public class Engine {

    public static final boolean DEV_BUILD = false;

    private SystemManager manager;

    private boolean running = true;

    private UpdateTimer timer;

    public Engine() {
        init();

//        for (int y = 0; y < 20; y++)
//            for (int x = 0; x < 20; x++)
//                EventSystem.publish(new AddEntityEvent(new Entity(new Vector2f((x - 10f) / 10f, (y - 10f) / 10f), new Vector2f(.1f, .1f), 0).addComponent(new SpriteComponent(1))));

        EventSystem.publish(new AddEntityEvent(new Entity(new Vector2f(0, 0)).addComponent(new ScriptComponent("lua/test.lua")).addComponent(new SpriteComponent(1))));

        while (running) {
            while (timer.shouldUpdateFPS())
                update((float) timer.getDelta());

            if (timer.shouldUpdateSecond())
                Logger.debug("Engine", "FPS: " + timer.getFrames() + ", UPS: " + timer.getUpdates());

            render();
        }

        manager.destroySystems();
    }

    private void init() {
        NativesLoader.loadNatives();

        ResourceManager.init();

        manager = new SystemManager();
        manager.addSystem(EventSystem.class);
        manager.addSystem(ProcessSystem.class);
        manager.addSystem(GraphicsSystem.class);
        manager.addSystem(InputSystem.class);
        manager.addSystem(EntitySystem.class);
        manager.addSystem(ScriptSystem.class);
        manager.initSystems();


        Logger.init();
        Logger.info("Engine", "Starting engine");

        EventSystem.subscribe(this);

        XMLConfigFile engineConfig = new XMLConfigFile("config/engine.xml");
        engineConfig.load();

        timer = new UpdateTimer(engineConfig.getInt("target_fps"));
    }

    private void update(float delta) {
        manager.updateSystems(delta);
    }

    private void render() {
        manager.renderSystems();
    }

    @Handler
    private void onExitReceived(EngineStateEvent event) {
        switch (event.state) {
            case STOP:
                running = false;
                break;

            default:
                Logger.warn("Engine", "Engine state not handled");
        }
    }

    public static void main(String[] args) {
        new Engine();
    }

}
