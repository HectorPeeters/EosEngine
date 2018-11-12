package com.hector.engine.graphics;

import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.components.SpriteComponent;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.maths.Matrix3f;
import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class GraphicsSystem extends AbstractSystem {

    //TODO: use index buffer

    private float[] vertices = new float[] {
            -0.5f, 0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,

            -0.5f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f,
    };

    private float[] texCoords = new float[] {
            1, 0,
            0, 1,
            0, 0,

            1, 0,
            1, 1,
            0, 1,
    };

    private Display display;

    private Shader shader;

    private List<SpriteComponent> spriteComponents = new ArrayList<>();

    public GraphicsSystem() {
        super("graphics", 1500);
    }

    //TODO: fix rotation sheering!!!

    @Override
    protected void init() {
        display = new Display();

        int displayWidth = config.getInt("width");
        int displayHeight = config.getInt("height");
        float aspectRatio = displayWidth / (float) displayHeight;
        display.create(displayWidth, displayHeight);

        GL11.glEnable(GL20.GL_MULTISAMPLE);

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

        int vboId2 = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId2);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, texCoords, GL30.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        GL30.glEnableVertexAttribArray(1);

        shader.setInt("sampler", 0);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    private boolean pressed = false;

    @Handler
    private void onKeyEvent(KeyEvent event) {
        if (event.keycode == GLFW.GLFW_KEY_SPACE)
            pressed = event.pressed;
    }

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        if (pressed)
            return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (SpriteComponent component : spriteComponents) {
            shader.bind();

            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, component.textureId);

            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);

            Matrix3f transformationMatrix = component.getParent().getTransformationMatrix();
            shader.setMatrix3f("transformationMatrix", transformationMatrix);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertices.length);

            GL20.glDisableVertexAttribArray(1);
            GL20.glDisableVertexAttribArray(0);

            shader.unbind();
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
