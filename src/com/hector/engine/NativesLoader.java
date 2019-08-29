package com.hector.engine;

import java.io.File;

public class NativesLoader {

    private static final String PLATFORM = "linux";

    public static void loadNatives() {
        File nativesFolder = new File("natives/natives-" + PLATFORM);
        System.setProperty("org.lwjgl.librarypath", nativesFolder.getAbsolutePath());

//        for (File f : nativesFolder.listFiles()) {
//            System.out.println(f.getName());
//        }
    }
}
