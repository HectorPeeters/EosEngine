package com.hector.engine.physics.components;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.maths.Vector2f;

public class RigidBodyComponent extends AbstractEntityComponent {

    private Vector2f velocity;
    private Vector2f acceleration;

    private float mass;

    private boolean isStatic = false;

    public RigidBodyComponent() {
        this.mass = 1;
        this.velocity = new Vector2f(0, 0);
        this.acceleration = new Vector2f(0, 0);
    }

    public RigidBodyComponent(float mass) {
        this.mass = mass;
        this.velocity = new Vector2f(0, 0);
        this.acceleration = new Vector2f(0, 0);
    }

    public void addForce(Vector2f force) {
        acceleration = acceleration.add(force.div(mass));
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public void setPosition(Vector2f position) {
        parent.setPosition(position);
    }

    public Vector2f getPosition() {
        return parent.getPosition();
    }

    public Vector2f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2f velocity) {
        this.velocity = velocity;
    }

    public Vector2f getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector2f acceleration) {
        this.acceleration = acceleration;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }
}
