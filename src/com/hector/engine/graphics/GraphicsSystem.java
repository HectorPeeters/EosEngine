package com.hector.engine.graphics;

import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.components.SpriteComponent;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.logging.Logger;
import com.hector.engine.maths.Matrix3f;
import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class GraphicsSystem extends AbstractSystem {

    //TODO: use index buffer

    private float[] vertices = new float[]{
            -0.5f, 0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,

            -0.5f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f,
    };

    private float[] texCoords = new float[]{
            1, 1,
            0, 0,
            0, 1,

            1, 1,
            1, 0,
            0, 0,
    };

    private Display display;

    private Shader shader;
    private Shader screenShader;

    private FrameBuffer frameBuffer;

    private int quadVAOId;

    private Model quadModel;

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

        shader = new Shader("basic");
        shader.setInt("sampler", 0);
        Matrix3f orthographic = new Matrix3f().initOrtho(-1 * aspectRatio, 1 * aspectRatio, 1, -1, -1, 1);
        shader.bind().setMatrix3f("orthographicMatrix", orthographic);
        shader.unbind();

        screenShader = new Shader("fbo");
        screenShader.setInt("screenTexture", 1);

        quadModel = new Model(vertices, texCoords);

        if (isFrameBufferSupported())
            frameBuffer = new FrameBuffer(displayWidth, displayHeight);
        else
            Logger.err("Graphics", "Framebuffer not supported");

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
        if (pressed)
            return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        //First pass
        frameBuffer.bind();

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

//        GL11.glColor3f(1, 0, 0);
//        GL11.glBegin(GL11.GL_TRIANGLES);
//        GL11.glVertex2f(-0.5f, -0.5f);
//        GL11.glVertex2f(0.5f, -0.5f);
//        GL11.glVertex2f(0, 0.8f);
//        GL11.glEnd();

        drawScene();

        frameBuffer.unbind();

        //Second pass
        screenShader.bind();

        quadModel.bind();
        quadModel.enableVertexAttribArrays();

        GL30.glBindVertexArray(quadModel.getVaoId());
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, frameBuffer.getTextureId());
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quadModel.getVertexCount());
        GL30.glBindVertexArray(0);

        quadModel.disableVertexAttribArrays();
        quadModel.unbind();

        screenShader.unbind();
    }

    private void drawScene() {
        quadModel.bind();
        quadModel.enableVertexAttribArrays();

        shader.bind();

        for (SpriteComponent component : spriteComponents) {
            GL20.glActiveTexture(GL20.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, component.textureId);

            Matrix3f transformationMatrix = component.getParent().getTransformationMatrix();
            shader.setMatrix3f("transformationMatrix", transformationMatrix);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quadModel.getVertexCount());
        }

        shader.unbind();

        quadModel.disableVertexAttribArrays();
        quadModel.unbind();
    }

    private boolean isFrameBufferSupported() {
        return GL.getCapabilities().GL_EXT_framebuffer_object;
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
        screenShader.destroy();

        quadModel.destroy();

        display.destroy();
    }
}
