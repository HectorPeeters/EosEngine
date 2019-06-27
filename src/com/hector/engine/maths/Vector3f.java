package com.hector.engine.maths;

public class Vector3f {


    public float x;
    public float y;
    public float z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f add(Vector3f other) {
        return new Vector3f(x + other.x, y + other.y, z + other.z);
    }

    public Vector3f sub(Vector3f other) {
        return new Vector3f(x - other.x, y - other.y, z - other.z);
    }

    public Vector3f mul(Vector3f other) {
        return new Vector3f(x * other.x, y * other.y, z * other.z);
    }

    public Vector3f div(Vector3f other) {
        return new Vector3f(x / other.x, y / other.y, z / other.z);
    }


    public Vector3f add(float other) {
        return new Vector3f(x + other, y + other, z + other);
    }

    public Vector3f sub(float other) {
        return new Vector3f(x - other, y - other, z - other);
    }

    public Vector3f mul(float other) {
        return new Vector3f(x * other, y * other, z * other);
    }

    public Vector3f div(float other) {
        return new Vector3f(x / other, y / other, z * other);
    }

    public float dot(Vector3f other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
    
}
