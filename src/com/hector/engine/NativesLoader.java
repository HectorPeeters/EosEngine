package com.hector.engine;

import java.io.File;

public class NativesLoader {

    private static final String PLATFORM = "linux";

    public static void loadNatives() {
        System.setProperty("org.lwjgl.librarypath", new File("natives/natives-" + PLATFORM).getAbsolutePath());
    }
}
