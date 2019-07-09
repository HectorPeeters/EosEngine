package com.hector.tools.codelist;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CodeList {

    public CodeList() {
        List<File> files = sourceFiles(new File("src/"));

        StringBuilder fullSource = new StringBuilder();
        for (File f : files) {
            fullSource.append(readFile(f)).append("\n");
        }

        try {
            FileWriter fw = new FileWriter(new File("allSource.txt"));
            fw.append(fullSource.toString());
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(File f) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public List<File> sourceFiles(File file) {
        List<File> result = new ArrayList<>();

        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                result.addAll(sourceFiles(f));
            } else {
                if (f.getName().endsWith(".java"))
                    result.add(f);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        new CodeList();
    }

}
