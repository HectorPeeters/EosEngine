package com.hector.engine;

import java.io.File;

public class NativesLoader {

    public static void loadNatives() {
        File nativesFolder = new File("natives/");
        System.setProperty("org.lwjgl.librarypath", nativesFolder.getAbsolutePath());
    }
}
