package com.hector.engine;

import java.io.File;

/**
 * Basic utility class for loading the native libraries
 * @author HectorPeeters
 */
public class NativesLoader {

    /**
     * Simple method which loads all native libraries
     */
    public static void loadNatives() {
        File nativesFolder = new File("natives/");
        System.setProperty("org.lwjgl.librarypath", nativesFolder.getAbsolutePath());
    }
}
