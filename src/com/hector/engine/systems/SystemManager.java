package com.hector.engine.systems;

import com.hector.engine.event.EventSystem;
import com.hector.engine.logging.Logger;
import com.hector.engine.profiling.Profiling;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class manages the initialisation, update and cleanup of all engine systems.
 *
 * @author HectorPeeters
 */
public class SystemManager {

    /**
     * A {@link CopyOnWriteArrayList} to store all {@link AbstractSystem} instances.
     */
    public List<AbstractSystem> systems = new CopyOnWriteArrayList<>();

    /**
     * Tries to create an instance of the given system class and adds it to the systems list.
     *
     * @param systemClass The class of the system to add
     */
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

    /**
     * Helper method to add a system in the correct place in the systems list. This method makes sure that all systems
     * are in the correct priority order.
     * @param system    The system to add to the list
     */
    private void addSystemToArray(AbstractSystem system) {
        int priority = system.getInitPriority();

        if (systems.contains(system))
            return;

        for (int i = 0; i < systems.size(); i++) {
            if (systems.get(i).getInitPriority() < priority) continue;

//            if (systems.get(i).getInitPriority() == priority)

            systems.add(i, system);
            return;
        }

        systems.add(system);
    }

    /**
     * Updates all the systems and profiles them
     * @param delta The delta time used for updating each system
     */
    public void updateSystems(float delta) {
        for (AbstractSystem system : systems)
            system.preUpdate(delta);

        for (AbstractSystem system : systems) {
            Profiling.start("System " + system.getName());
            system.update(delta);
            Profiling.stop("System " + system.getName());
        }

        for (AbstractSystem system : systems)
            system.postUpdate(delta);
    }

    /**
     * Renders all the systems
     */
    public void renderSystems() {
        for (AbstractSystem system : systems)
            system.preRender();

        for (AbstractSystem system : systems)
            system.render();

        for (AbstractSystem system : systems)
            system.postRender();
    }

    /**
     * Inits all the systems, profiles the initialization and subscribes each system for events.
     */
    public void initSystems() {
        for (AbstractSystem system : systems) {
            long initStartTime = System.currentTimeMillis();

            system.initModule();
            EventSystem.subscribe(system);

            Logger.debug("System", "Initialized " + system.getName() + " system in " + (System.currentTimeMillis() - initStartTime) + "ms");
        }

        Logger.info("System", "Initialized " + systems.size() + " system" + (systems.size() == 1 ? "" : "s"));

        EventSystem.subscribe(this);
    }

    /**
     * Destroys all systems and clears the systems list.
     */
    public void destroySystems() {
        for (int i = systems.size() - 1; i >= 0; i--)
            systems.get(i).destroy();

        Logger.info("System", "Destroyed " + systems.size() + " system" + (systems.size() == 1 ? "" : "s"));

        systems.clear();
    }
}
