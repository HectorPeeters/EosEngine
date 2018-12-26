package com.hector.engine.resource.markup;

import com.hector.engine.maths.Vector2f;

import java.util.Map;

public class MarkupNode {

    public String name;
    public Object value;
    public MarkupNodeType type;

    public enum MarkupNodeType {
        STRING,
        INT,
        FLOAT,
        BOOLEAN,
        VECTOR2F,
        ARRAY
    }

    public MarkupNode(String name, Object value, MarkupNodeType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public int getInt() {
        return Integer.parseInt(value.toString());
    }

    public float getFloat() {
        return Float.parseFloat(value.toString());
    }

    public boolean getBoolean() {
        return Boolean.parseBoolean(value.toString());
    }

    public String getString() {
        return value.toString();
    }

    public Vector2f getVector2f() {
        return (Vector2f) value;
    }

    public MarkupNodeList getArray() {
        return (MarkupNodeList) value;
    }
    @Override
    public String toString() {
        return "MarkupNode{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", type=" + type +
                '}';
    }
}
