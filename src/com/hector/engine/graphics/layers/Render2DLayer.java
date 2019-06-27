package com.hector.engine.graphics.layers;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.Camera;
import com.hector.engine.graphics.Mesh;
import com.hector.engine.graphics.Shader;
import com.hector.engine.graphics.components.AnimationComponent;
import com.hector.engine.graphics.components.TextureComponent;
import com.hector.engine.maths.Matrix3f;
import com.hector.engine.maths.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.List;

public class Render2DLayer extends RenderLayer {

    //Use index buffer if performance problems occur
    private final float[] vertices = new float[]{
            -0.5f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f,

            0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
    };

    private final float[] textureCoords = new float[]{
            0, 0,
            0, 1,
            1, 1,

            1, 1,
            1, 0,
            0, 0,
    };

    private Shader shader;

    private Mesh quadMesh;

    private List<AbstractEntityComponent> textureComponents = new ArrayList<>();

    private float aspectRatio;

    public Render2DLayer(int width, int height) {
        aspectRatio = width / (float) height;
    }

    @Override
    public void init() {
        shader = new Shader("animation");
        shader.bind();
        shader.setInt("sampler", 0);
        Matrix3f ortho = new Matrix3f().initOrtho(-1 * aspectRatio, 1 * aspectRatio, 1, -1, -1, 1);
        shader.setMatrix3f("orthographicMatrix", ortho);
        shader.unbind();

        EventSystem.subscribe(this);

        quadMesh = new Mesh(vertices, textureCoords);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glClearColor(0.3f, 0.3f, 0.3f, 1);
    }


    @Override
    public void update(float delta) {
        for (AbstractEntityComponent component : textureComponents) {
            if (component instanceof AnimationComponent)
                ((AnimationComponent) component).update(delta);
        }
    }

    @Override
    public void render() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL20.glActiveTexture(GL20.GL_TEXTURE0);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        shader.bind();

        quadMesh.bind();
        quadMesh.enableVertexAttribArrays();

        for (AbstractEntityComponent component : textureComponents) {
            Matrix3f transformationMatrix = component.getParent().getTransformationMatrix();

            shader.setMatrix3f("transformationMatrix", transformationMatrix);
            shader.setMatrix3f("cameraMatrix", Camera.main.getCameraMatrix());

            if (component instanceof TextureComponent) {
                TextureComponent texture = (TextureComponent) component;
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.texture.getId());

                shader.setVector4f("animationData", new Vector4f(1, 1, 0, 0));
            } else if (component instanceof AnimationComponent) {
                AnimationComponent animation = (AnimationComponent) component;
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, animation.getTextureId());

                shader.setVector4f("animationData", new Vector4f(animation.getFramesWide(), animation.getFramesHigh(), animation.getFrameIndex(), animation.isFlipped() ? 1 : 0));
            }

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quadMesh.getVertexCount());
        }

        shader.unbind();
    }


    @Override
    public void onEvent(LayerInputEvent event) {

    }

    @Override
    public void destroy() {
        shader.destroy();
    }

    @Handler
    private void onSpriteComponentAdded(AddEntityComponentEvent event) {
        for (AbstractEntityComponent comp : event.components) {
            if (comp instanceof TextureComponent || comp instanceof AnimationComponent) {
                textureComponents.add(comp);
            }
        }
    }

    @Handler
    private void onSpriteComponentRemoved(RemoveEntityComponentEvent event) {
        for (AbstractEntityComponent comp : event.components) {
            if (comp instanceof TextureComponent || comp instanceof AnimationComponent)
                textureComponents.remove(comp);
        }
    }

}
