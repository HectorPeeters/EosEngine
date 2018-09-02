package com.hector.engine.systems;

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

        addSystem(system);
    }

    private void addSystem(AbstractSystem system) {
        int priority = system.getInitPriority();

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
            system.update(delta);
    }

    public void renderSystems() {
        for (AbstractSystem system : systems)
            system.render();
    }

    public void initSystems() {
        for (AbstractSystem system : systems)
            system.init();
    }

    public void destroySystems() {
        for (int i = systems.size() - 1; i >= 0; i--)
            systems.get(i).destroy();

        systems.clear();
    }
}