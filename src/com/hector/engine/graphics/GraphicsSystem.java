package com.hector.engine.graphics;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.components.AnimationComponent;
import com.hector.engine.graphics.components.TextureComponent;
import com.hector.engine.logging.Logger;
import com.hector.engine.maths.Matrix3f;
import com.hector.engine.maths.Vector4f;
import com.hector.engine.physics.components.RigidbodyComponent;
import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class GraphicsSystem extends AbstractSystem {

    //TODO: use index buffer if performance problems occur

    private float[] vertices = new float[]{
            -0.5f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f,

            0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
    };

    private float[] texCoords = new float[]{
            0, 0,
            0, 1,
            1, 1,

            1, 1,
            1, 0,
            0, 0,
    };

    private Display display;

    private Shader shader;
    private Shader fboShader;
    private Shader animationShader;

    private FrameBuffer frameBuffer;

    private Mesh quadMesh;

    private List<AbstractEntityComponent> textureComponents = new ArrayList<>();

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
        shader.bind();
        shader.setInt("sampler", 0);
        Matrix3f orthographic = new Matrix3f().initOrtho(-1 * aspectRatio, 1 * aspectRatio, 1, -1, -1, 1);
        shader.setMatrix3f("orthographicMatrix", orthographic);
        shader.unbind();

        fboShader = new Shader("fbo");
        fboShader.setInt("sampler", 1);

        animationShader = new Shader("animation");
        animationShader.bind();
        animationShader.setInt("sampler", 0);
        animationShader.setMatrix3f("orthographicMatrix", orthographic);
        animationShader.unbind();

        quadMesh = new Mesh(vertices, texCoords);

        if (isFrameBufferSupported())
            frameBuffer = new FrameBuffer(displayWidth, displayHeight);
        else
            Logger.err("Graphics", "Framebuffer not supported");

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);

        Logger.info("Graphics", "LWJGL Version: " + Version.getVersion());
    }

    @Override
    public void update(float delta) {
        for (AbstractEntityComponent comp : textureComponents) {
            if (comp instanceof AnimationComponent)
                ((AnimationComponent) comp).update(delta);
        }
    }

    @Override
    public void render() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        //First pass
        frameBuffer.bind();

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        drawSprites();

        frameBuffer.unbind();

        //Second pass
        fboShader.bind();

        quadMesh.bind();
        quadMesh.enableVertexAttribArrays();

        GL30.glBindVertexArray(quadMesh.getVaoId());
        GL20.glBindTexture(GL20.GL_TEXTURE_2D, frameBuffer.getTextureId());
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quadMesh.getVertexCount());
        GL30.glBindVertexArray(0);

        quadMesh.disableVertexAttribArrays();
        quadMesh.unbind();

        fboShader.unbind();
    }

    private void drawSprites() {
        quadMesh.bind();
        quadMesh.enableVertexAttribArrays();

        animationShader.bind();

        for (AbstractEntityComponent component : textureComponents) {
            GL20.glActiveTexture(GL20.GL_TEXTURE0);

            Matrix3f transformationMatrix = component.getParent().getTransformationMatrix();

            animationShader.setMatrix3f("transformationMatrix", transformationMatrix);
            animationShader.setMatrix3f("cameraMatrix", Camera.main.getCameraMatrix());

            if (component instanceof AnimationComponent) {
                AnimationComponent comp = (AnimationComponent) component;

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, comp.getTextureId());
                animationShader.setVector4f("animationData", new Vector4f(comp.getFramesWide(),
                        comp.getFramesHigh(), comp.getFrameIndex(), comp.isFlipped() ? 1 : 0));
            } else if (component instanceof TextureComponent){
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, ((TextureComponent) component).textureId);
                animationShader.setVector4f("animationData", new Vector4f(1, 1, 0, 0));
            }

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quadMesh.getVertexCount());
        }

        animationShader.unbind();

        quadMesh.disableVertexAttribArrays();
        quadMesh.unbind();
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
        for (AbstractEntityComponent comp : event.components) {
            if (comp instanceof TextureComponent || comp instanceof AnimationComponent)
                textureComponents.add(comp);
        }
    }

    @Handler
    private void onSpriteComponentRemoved(RemoveEntityComponentEvent event) {
        for (AbstractEntityComponent comp : event.components) {
            if (comp instanceof TextureComponent || comp instanceof AnimationComponent)
                textureComponents.remove(comp);
        }
    }

    @Override
    protected void reset() {
        textureComponents.clear();
    }

    @Override
    protected void destroy() {
        shader.destroy();
        fboShader.destroy();
        animationShader.destroy();

        frameBuffer.destroy();

        quadMesh.destroy();

        display.destroy();
    }
}
