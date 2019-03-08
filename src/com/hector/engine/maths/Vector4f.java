package com.hector.engine.maths;

public class Vector4f {

    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4f add(Vector4f other) {
        return new Vector4f(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Vector4f sub(Vector4f other) {
        return new Vector4f(x - other.x, y - other.y, z - other.z, w - other.w);
    }

    public Vector4f mul(Vector4f other) {
        return new Vector4f(x * other.x, y * other.y, z * other.z, w * other.w);
    }

    public Vector4f div(Vector4f other) {
        return new Vector4f(x / other.x, y / other.y, z / other.z, w / other.w);
    }


    public Vector4f add(float other) {
        return new Vector4f(x + other, y + other, z + other, w + other);
    }

    public Vector4f sub(float other) {
        return new Vector4f(x - other, y - other, z - other, w - other);
    }

    public Vector4f mul(float other) {
        return new Vector4f(x * other, y * other, z * other, w * other);
    }

    public Vector4f div(float other) {
        return new Vector4f(x / other, y / other, z * other, w * other);
    }

    public float dot(Vector4f other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
