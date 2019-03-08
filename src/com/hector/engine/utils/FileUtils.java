package com.hector.engine.utils;

import com.hector.engine.logging.Logger;

import java.io.*;

/**
 * This class handles all types of file IO utilities like loading files with #include statements
 */
public class FileUtils {

    /**
     * Loads a text file and processes includeTags. These tags can be anything. Most likely something like "#include".
     * @param path The path of the file that needs to be loaded
     * @param includeTag The special tags to process
     * @return The string of the full file with included files
     */
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
