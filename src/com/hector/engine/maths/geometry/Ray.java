package com.hector.engine.maths.geometry;

import com.hector.engine.maths.Vector2f;

public class Ray {

    protected Vector2f origin;
    protected Vector2f direciton;

    public Ray(Vector2f origin, Vector2f direciton) {
        this.origin = origin;
        this.direciton = direciton;
    }

    public Ray(Vector2f origin, float angle) {
        this.origin = origin;
        this.direciton = new Vector2f((float) Math.cos(Math.toRadians(angle)), (float) Math.sin(Math.toRadians(angle)));
    }

}
