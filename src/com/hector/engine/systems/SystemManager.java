package com.hector.engine.systems;

import com.hector.engine.event.EventSystem;
import com.hector.engine.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class SystemManager {

    public List<AbstractSystem> systems = new ArrayList<>();

    public void addSystem(Class<? extends AbstractSystem> systemClass) {
        AbstractSystem system;

        try {
            system = systemClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            Logger.err("System", "Failed to instantiate system: " + systemClass.getSimpleName());
            return;
        }

        addSystemToArray(system);
    }

    private void addSystemToArray(AbstractSystem system) {
        int priority = system.getInitPriority();

        if (systems.contains(system))
            return;

        for (int i = 0; i < systems.size(); i++) {
            if (systems.get(i).getInitPriority() < priority) continue;

            if (systems.get(i).getInitPriority() == priority) return;

            systems.add(i, system);
            return;
        }

        systems.add(system);
    }

    public void updateSystems(float delta) {
        for (AbstractSystem system : systems)
            system.preUpdate(delta);

        for (AbstractSystem system : systems)
            system.update(delta);

        for (AbstractSystem system : systems)
            system.postUpdate(delta);
    }

    public void renderSystems() {
        for (AbstractSystem system : systems)
            system.preRender();

        for (AbstractSystem system : systems)
            system.render();

        for (AbstractSystem system : systems)
            system.postRender();
    }

    public void initSystems() {
        for (AbstractSystem system : systems) {
            long initStartTime = System.currentTimeMillis();

            system.initModule();
            EventSystem.subscribe(system);

            Logger.debug("System", "Initialized " + system.name + " system in " + (System.currentTimeMillis() - initStartTime) + "ms");
        }

        Logger.info("System", "Initialized " + systems.size() + " system" + (systems.size() == 1 ? "" : "s"));
    }

    public void destroySystems() {
        for (int i = systems.size() - 1; i >= 0; i--)
            systems.get(i).destroyModule();

        Logger.info("System", "Destroyed " + systems.size() + " system" + (systems.size() == 1 ? "" : "s"));

        systems.clear();
    }
}
