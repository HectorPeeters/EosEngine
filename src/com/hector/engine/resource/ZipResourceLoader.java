package com.hector.engine.resource;

import com.hector.engine.logging.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceLoader extends AbstractResourceLoader {

    private static final String ZIP_FILE = "Assets.zip";

    private ZipFile zipFile;

    @Override
    public String loadText(String path) {
        setupZipFile();

        ZipEntry entry = zipFile.getEntry(path);
        if (entry == null) {
            Logger.err("Resource", "Zip entry " + path + " not found");
            return null;
        }

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            br.close();

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("Resource", "Could not retrieve input stream for zip resource " + entry.getName());
        }

        return null;
    }

    @Override
    public boolean doesFileExist(String path) {
        setupZipFile();
        return zipFile.getEntry(path) != null;
    }

    @Override
    public String[] listFiles(String path) {
        throw new NotImplementedException();
    }

    private void setupZipFile() {
        if (zipFile != null)
            return;

        try {
            zipFile = new ZipFile(ZIP_FILE);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("Resource", "Zip file " + ZIP_FILE + " does not exist");
        }
    }
}
