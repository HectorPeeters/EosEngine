package com.hector.engine.graphics.layers;

import com.hector.engine.graphics.Camera;
import com.hector.engine.graphics.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Render3DLayer extends AbstractRenderLayer {

    private Camera camera;
    private Matrix4f projectionMatrix;

    private int width, height;

    private ShaderProgram shader;

    public Render3DLayer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void init() {
        camera = new Camera(new Vector3f(0, 0, 0), 1f);

        projectionMatrix = new Matrix4f().perspective(43, width / (float) height, 0.1f, 1000f);

        shader = new ShaderProgram("3d");
        shader.bind();
        shader.setMatrix4f("projectionMatrix", projectionMatrix);
        shader.unbind();

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render() {

    }

    @Override
    public void onEvent(LayerInputEvent event) {

    }

    @Override
    public void destroy() {

    }
}
