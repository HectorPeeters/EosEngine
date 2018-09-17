package com.hector.engine.utils;

import com.hector.engine.logging.Logger;

import java.io.*;

public class FileUtils {

    public static String loadWithInclude(String path, String includeTag) {
        File file = new File(path);
        if (!file.exists()) {
            Logger.err("IO", "File \'" + path + "\' does not exist");
            return null;
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logger.err("IO", "Failed to create FileReader of file \'" + file + "\'");
            return null;
        }

        StringBuilder buffer = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                if (line.trim().startsWith(includeTag)) {
                    String includeFile = line.trim().replace(includeTag, "").trim();

                    buffer.append(loadWithInclude(includeFile, includeTag)).append("\n");
                    continue;
                }

                buffer.append(line).append("\n");
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("IO", "Failed to read line of file \'" + file + "\'");
            return null;
        }

        return buffer.toString();
    }

}
