package com.hector.engine.graphics;

import com.hector.engine.maths.Matrix3f;
import com.hector.engine.maths.Vector2f;

public class Camera {

    public static Camera main = new Camera(new Vector2f(0, 0), 5);

    private Vector2f position;
    private float size;
    private float rotation = 0f;

    public Camera(Vector2f position, float size) {
        this.position = position;
        this.size = size;
    }

    public Matrix3f getCameraMatrix() {
        //TODO: try and fix this mess with .div(size)
        return new Matrix3f().initTransformation(position.mul(-1).div(size), new Vector2f(1 / size, 1 / size), -rotation);
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setPosition(Vector2f position) {
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
