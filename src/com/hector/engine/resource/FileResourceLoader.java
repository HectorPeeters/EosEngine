package com.hector.engine.resource;

import com.hector.engine.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileResourceLoader extends AbstractResourceLoader {

    private static final String ASSET_DIR = "assets/";

    @Override
    public String[] listFiles(String path) {
        throw new NotImplementedException();
    }

    @Override
    public String loadText(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(ASSET_DIR + path)));

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            br.close();

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("Resource", "Failed to load text file " + path);
        }

        return null;
    }

    @Override
    public boolean doesFileExist(String path) {
        return new File(ASSET_DIR + path).exists();
    }
}
