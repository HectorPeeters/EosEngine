package com.hector.engine.graphics;

import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.components.SpriteComponent;
import com.hector.engine.maths.Matrix3f;
import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GraphicsSystem extends AbstractSystem {

    private Display display;

    private Shader shader;

    private List<SpriteComponent> spriteComponents = new ArrayList<>();

    public GraphicsSystem() {
        super("graphics", 1500);
    }

    @Override
    protected void init() {
        display = new Display();

        int displayWidth = config.getInt("width");
        int displayHeight = config.getInt("height");
        float aspectRatio = displayWidth / (float) displayHeight;
        display.create(displayWidth, displayHeight);

        shader = new Shader("basic");

        //Setup orthographic projection matrix
        Matrix3f orthographic = new Matrix3f().initOrtho(-1 * aspectRatio, 1 * aspectRatio, 1, -1, -1, 1);
        shader.bind().setMatrix3f("orthographicMatrix", orthographic);
    }

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glLoadIdentity();

        shader.bind();

        for (SpriteComponent component : spriteComponents) {
            Matrix3f transformationMatrix = component.getParent().getTransformationMatrix();
            shader.setMatrix3f("transformationMatrix", transformationMatrix);

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glVertex2f(-0.5f, -0.5f);
            GL11.glVertex2f(-0.5f, 0.5f);
            GL11.glVertex2f(0.5f, 0.5f);
            GL11.glVertex2f(0.5f, -0.5f);
            GL11.glEnd();
        }

        shader.unbind();
    }

    @Override
    public void postRender() {
        display.update();
    }

    @Handler
    private void onSpriteComponentAdded(AddEntityComponentEvent event) {
        if (!(event.component instanceof SpriteComponent))
            return;

        spriteComponents.add((SpriteComponent) event.component);
    }

    @Handler
    private void onSpriteComponentRemoved(RemoveEntityComponentEvent event) {
        if (!(event.component instanceof SpriteComponent))
            return;

        spriteComponents.remove(event.component);
    }

    @Override
    protected void destroy() {
        shader.destroy();

        display.destroy();
    }
}
