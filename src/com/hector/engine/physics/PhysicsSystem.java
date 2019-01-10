package com.hector.engine.physics;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.event.Handler;
import com.hector.engine.maths.Vector2f;
import com.hector.engine.physics.components.RigidbodyComponent;
import com.hector.engine.systems.AbstractSystem;

import java.util.ArrayList;
import java.util.List;

public class PhysicsSystem extends AbstractSystem {

    private final Vector2f gravity = new Vector2f(0, -9.81f);

    private List<RigidbodyComponent> rigidBodyComponents;

    public PhysicsSystem() {
        super("physics", 3500);
    }

    @Override
    protected void init() {
        rigidBodyComponents = new ArrayList<>();
    }

    @Override
    public void update(float delta) {
        for (RigidbodyComponent rb : rigidBodyComponents) {
            if (!rb.isStatic()) {
                rb.setVelocity(rb.getVelocity().add(rb.getAcceleration().mul(delta)));
                rb.setPosition(rb.getPosition().add(rb.getVelocity().mul(delta)));
            }
        }
    }

    @Handler
    private void onRigidBodyComponentAdd(AddEntityComponentEvent event) {
        for (AbstractEntityComponent comp : event.components) {
            if (comp instanceof RigidbodyComponent)
                rigidBodyComponents.add((RigidbodyComponent) comp);
        }
    }

    @Handler
    private void onRigidBodyComponentRemoved(RemoveEntityComponentEvent event) {
        for (AbstractEntityComponent comp : event.components) {
            if (comp instanceof RigidbodyComponent)
                rigidBodyComponents.remove(comp);
        }
    }

    @Override
    protected void reset() {
        rigidBodyComponents.clear();
    }

    @Override
    protected void destroy() {
        rigidBodyComponents.clear();
    }
}
