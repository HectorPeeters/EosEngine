package com.hector.engine.resource.markup;

import org.joml.Vector2f;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MarkupTest {

    public static void main(String[] args) {
        MarkupNodeList nodes = new MarkupParser().parse(
                "name test\n" +
                        "position 3.2 54.2f\n" +
                        "rotation 34.3\n" +
                        "scale 2\n" +
                        "subItems [\n" +
                        "   test 12\n" +
                        "   test2 13\n" +
                        "]");
        String name = nodes.getNode("name").getString();
        Vector2f position = nodes.getNode("position").getVector2f();
        float rotation = nodes.getNode("rotation").getFloat();
        int test2 = nodes.getNode("subItems").getArray().getNode("test").getInt();
        System.out.println(test2);

    }

    private static String loadFile(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));

            StringBuilder file = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                file.append(line).append("\n");
            }

            br.close();

            return file.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
