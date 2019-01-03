package com.hector.engine.maths;

public class Vector2f {

    public float x, y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }


    public Vector2f add(Vector2f other) {
        return new Vector2f(x + other.x, y + other.y);
    }

    public Vector2f sub(Vector2f other) {
        return new Vector2f(x - other.x, y - other.y);
    }

    public Vector2f mul(Vector2f other) {
        return new Vector2f(x * other.x, y * other.y);
    }

    public Vector2f div(Vector2f other) {
        return new Vector2f(x / other.x, y / other.y);
    }


    public Vector2f add(float other) {
        return new Vector2f(x + other, y + other);
    }

    public Vector2f sub(float other) {
        return new Vector2f(x - other, y - other);
    }

    public Vector2f mul(float other) {
        return new Vector2f(x * other, y * other);
    }

    public Vector2f div(float other) {
        return new Vector2f(x / other, y / other);
    }


    public float dot(Vector2f other) {
        return x * other.x + y * other.y;
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
