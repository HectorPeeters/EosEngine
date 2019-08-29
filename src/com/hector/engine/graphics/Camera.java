package com.hector.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    public static Camera main = new Camera(new Vector3f(0, 0, 1), 2);

    private Vector3f position;
    private float size;
    private float rotation = 0f;

    public Camera(Vector3f position, float size) {
        this.position = position;
        this.size = size;
    }

    public Matrix4f getCameraMatrix() {
        //TODO: try and fix this mess with .div(size)
        return new Matrix4f().translate(position.mul(-1 / size)).scale(1 / size);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

}
