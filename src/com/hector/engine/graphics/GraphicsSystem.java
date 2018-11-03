package com.hector.engine.graphics;

import com.hector.engine.maths.Matrix3f;
import com.hector.engine.maths.Vector2f;
import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.opengl.GL11;

public class GraphicsSystem extends AbstractSystem {

    private Display display;

    private Shader shader;

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

    float rotation = 0f;

    @Override
    public void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glLoadIdentity();


        Matrix3f transformationMatrix = new Matrix3f().initTransformation(new Vector2f(0, 0), new Vector2f(1f, 1), rotation);
        rotation += 0.002f;

        shader.bind();
        shader.setMatrix3f("transformationMatrix", transformationMatrix);

        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2f(-0.1f, -0.08f);
        GL11.glVertex2f(0, 0.1f);
        GL11.glVertex2f(0.1f, -0.08f);
        GL11.glEnd();

        shader.unbind();
    }

    @Override
    public void postRender() {
        display.update();
    }

    @Override
    protected void destroy() {
        shader.destroy();

        display.destroy();
    }
}
