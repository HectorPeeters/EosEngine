package com.hector.engine.resource.markup;

import com.hector.engine.maths.Vector2f;

import java.util.Arrays;
import java.util.Scanner;

public class MarkupParser {

    /*
    EXAMPLE MARKUP FILE

    name test
    scale 2
    rotation 34.3
    subItems [
        test 12
        kakske 13
    ]
     */

    public MarkupNodeList parse(String text) {
        MarkupNodeList result = new MarkupNodeList();

        Scanner scanner = new Scanner(text);

        while (scanner.hasNextLine()) {
            MarkupNode node = parseNextNode(scanner.nextLine(), scanner);

            System.out.println(node);

            result.add(node);
        }

        return result;
    }

    private MarkupNode parseNextNode(String line, Scanner scanner) {
        line = line.trim();
        String[] lineData = line.split(" ");

        System.out.println(Arrays.toString(lineData));

        if (isInt(lineData)) {              //INT
            return new MarkupNode(lineData[0], Integer.parseInt(lineData[1]), MarkupNode.MarkupNodeType.INT);

        } else if (isFloat(lineData)) {     //FLOAT
            return new MarkupNode(lineData[0], Float.parseFloat(lineData[1]), MarkupNode.MarkupNodeType.FLOAT);

        } else if (isBoolean(lineData)) {   //BOOLEAN
            return new MarkupNode(lineData[0], Boolean.parseBoolean(lineData[1]), MarkupNode.MarkupNodeType.BOOLEAN);

        } else if (isArray(lineData)) {     //ARRAY
            return new MarkupNode(lineData[0], parseArray(scanner), MarkupNode.MarkupNodeType.ARRAY);

        } else if (isVector2f(lineData)) {  //VECTOR2F
            return new MarkupNode(lineData[0], new Vector2f(Float.parseFloat(lineData[1]),
                    Float.parseFloat(lineData[2])), MarkupNode.MarkupNodeType.VECTOR2F);

        } else {                            //STRING
            return new MarkupNode(lineData[0], lineData[1], MarkupNode.MarkupNodeType.STRING);

        }
    }

    private MarkupNodeList parseArray(Scanner scanner) {
        String line;

        MarkupNodeList result = new MarkupNodeList();

        while (!(line = scanner.nextLine()).trim().equals("]")) {
            MarkupNode node = parseNextNode(line, scanner);

            result.add(node);
        }

        return result;
    }

    private boolean isVector2f(String[] lineData) {
        if (lineData.length != 3)
            return false;

        try {
            Float.parseFloat(lineData[1]);
            Float.parseFloat(lineData[2]);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private boolean isArray(String[] lineData) {
        if (lineData.length != 2)
            return false;

        return lineData[1].equals("[");
    }


    private boolean isBoolean(String[] lineData) {
        if (lineData.length != 2)
            return false;

        return lineData[1].toLowerCase().equals("true") || lineData[1].toLowerCase().equals("false");
    }


    private boolean isFloat(String[] lineData) {
        if (lineData.length != 2)
            return false;

        try {
            Float.parseFloat(lineData[1]);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private boolean isInt(String[] lineData) {
        if (lineData.length != 2)
            return false;

        try {
            Integer.parseInt(lineData[1]);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

}
