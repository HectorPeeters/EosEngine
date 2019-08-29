package com.hector.engine.physics.components;

import com.hector.engine.entity.AbstractEntityComponent;
import org.joml.Vector3f;

public class RigidbodyComponent extends AbstractEntityComponent {

    private Vector3f velocity;
    private Vector3f acceleration;

    private float mass;

    private boolean isStatic = false;

    public RigidbodyComponent() {
        this.mass = 1;
        this.velocity = new Vector3f(0, 0, 0);
        this.acceleration = new Vector3f(0, 0, 0);
    }

    public RigidbodyComponent(float mass) {
        this.mass = mass;
        this.velocity = new Vector3f(0, 0, 0);
        this.acceleration = new Vector3f(0, 0, 0);
    }

    public void addForce(Vector3f force) {
        acceleration = acceleration.add(force.mul(1 / mass));
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public void setPosition(Vector3f position) {
        parent.setPosition(position);
    }

    public Vector3f getPosition() {
        return parent.getPosition();
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public Vector3f getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3f acceleration) {
        this.acceleration = acceleration;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }
}
