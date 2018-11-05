package com.hector.engine.resource;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.resources.StreamResource;
import com.hector.engine.resource.resources.TextResource;

import java.io.*;

public class FileSystemResourceLoader extends AbstractResourceLoader {

    //TODO: move to config file
    private static final String ASSETS_DIR = "assets/";

    @Override
    public TextResource getTextResource(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(ASSETS_DIR + path)));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            br.close();

            return new TextResource(path, sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("Resource", "Failed to load text file: " + path);
        }

        return null;
    }

    @Override
    public StreamResource getStreamResource(String path) {
        try {
            return new StreamResource(path, new FileInputStream(new File(ASSETS_DIR + path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.err("Resource", "Failed to get stream resource " + path);
        }

        return null;
    }

    @Override
    public boolean doesFileExist(String path) {
        return new File(ASSETS_DIR + path).exists();
    }

    @Override
    public String[] listFiles(String path) {
        File[] files = new File(ASSETS_DIR + path).listFiles();
        if (files == null)
            return null;

        String[] fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++)
            fileNames[i] = files[i].getPath();

        return fileNames;
    }

}
