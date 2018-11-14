package com.hector.engine;

import com.hector.engine.entity.Entity;
import com.hector.engine.entity.EntitySystem;
import com.hector.engine.entity.events.AddEntityEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.GraphicsSystem;
import com.hector.engine.graphics.components.AnimationComponent;
import com.hector.engine.input.InputSystem;
import com.hector.engine.logging.Logger;
import com.hector.engine.maths.Vector2f;
import com.hector.engine.physics.PhysicsSystem;
import com.hector.engine.process.ProcessSystem;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.scripting.ScriptSystem;
import com.hector.engine.scripting.components.GroovyScriptComponent;
import com.hector.engine.systems.SystemManager;
import com.hector.engine.utils.UpdateTimer;
import com.hector.engine.xml.XMLConfigFile;

import java.util.ArrayList;
import java.util.List;

public class Engine {

    public static final boolean DEV_BUILD = false;

    private SystemManager manager;

    private boolean running = true;

    private UpdateTimer timer;

    public Engine() {
        init();

        List<Entity> entities = new ArrayList<>();

        Entity entity = new Entity(new Vector2f(0, 0), new Vector2f(0.5f, 0.5f))
                .addComponent(new GroovyScriptComponent("groovy/controller.groovy"))
                .addComponent(new AnimationComponent("textures/vent/vent.png", 5, 1));
        entities.add(entity);

//        for (int i = 0; i < 10; i++)
//            entities.add(new Entity(new Vector2f(0, 0), new Vector2f(0.3f, 0.3f))
//                    .addComponent(new GroovyScriptComponent("groovy/test.groovy"))
//                    .addComponent(new RigidBodyComponent(10))
//                    .addComponent(new SpriteComponent("textures/engineer.png"))
//            );

        EventSystem.publish(new AddEntityEvent(entities));

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
        manager.initSystems();

        Logger.init();
        Logger.info("Engine", "Starting engine");

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
