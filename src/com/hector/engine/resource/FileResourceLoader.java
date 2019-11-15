package com.hector.engine.resource;

import com.hector.engine.logging.Logger;

import java.io.*;

public class FileResourceLoader extends AbstractResourceLoader {

    private static final String ASSET_DIR = "../../../assets/";

    @Override
    public String[] listFiles(String path) {
        try {
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
    public InputStream getInputStream(String path) {
        try {
            return new FileInputStream(new File(ASSET_DIR + path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.err("Resource", "Failed to get input stream from " + path);
        }
        return null;
    }

    @Override
    public boolean doesFileExist(String path) {
        return new File(ASSET_DIR + path).exists();
    }

    @Override
    public long getFileSize(String path) {
        return new File(ASSET_DIR + path).length();
    }
}
