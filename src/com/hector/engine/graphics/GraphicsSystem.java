package com.hector.engine.graphics;

import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.components.SpriteComponent;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.maths.Matrix3f;
import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

public class GraphicsSystem extends AbstractSystem {

    private float[] vertices = new float[] {
            -0.5f, -0.5f,
            0.5f, -0.5f,
            -0.5f, 0.5f,
            0.5f, 0.5f,
    };

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

        Matrix3f orthographic = new Matrix3f().initOrtho(-1 * aspectRatio, 1 * aspectRatio, 1, -1, -1, 1);
        shader.bind().setMatrix3f("orthographicMatrix", orthographic);

        int vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        int vboId = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertices, GL30.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);

        GL30.glEnableVertexAttribArray(0);
    }

    private boolean pressed = false;

    @Handler
    private void keyPress(KeyEvent event) {
        if (event.keycode == GLFW.GLFW_KEY_SPACE)
            pressed = event.pressed;
    }

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if (pressed)
            return;

        for (SpriteComponent component : spriteComponents) {
            Matrix3f transformationMatrix = component.getParent().getTransformationMatrix();
            shader.setMatrix3f("transformationMatrix", transformationMatrix);

            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, vertices.length);
        }
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
