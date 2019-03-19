package com.hector.tools.codelist;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CodeList {

    public CodeList() {
        List<File> files = sourceFiles(new File("src/"));

        String fullSource = "";
        for (File f : files) {
            fullSource += readFile(f) + "\n";
        }

        try {
            FileWriter fw = new FileWriter(new File("allSource.txt"));
            fw.append(fullSource);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(File f) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                result += line + "\n";
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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