package com.hector.engine;

import com.hector.engine.logging.Logger;

public class Engine {

    public static void main(String[] args) {
        Logger.init("config/logging.xml");

        Logger.info("Engine", "Starting engine");
    }

}
