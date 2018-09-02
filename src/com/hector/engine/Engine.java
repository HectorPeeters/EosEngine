package com.hector.engine;

import com.hector.engine.logging.Logger;
import com.hector.engine.systems.SystemManager;

public class Engine {

    public static void main(String[] args) {
        Logger.init("assets/config/logging.xml");

        Logger.info("Engine", "Starting engine");

        SystemManager manager = new SystemManager();

        manager.initSystems();

        manager.destroySystems();
    }

}
