package com.hector.engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

public class FrameBuffer {

    private int width, height;
    private int id;

    public FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;

        createTexture();
    }

    private void createTexture() {
        this.id = GL20.glGenTextures();

        bind();
        {
            GL43.glTexStorage2D(GL11.GL_TEXTURE_2D, 1, GL30.GL_RGBA32F, width, height);
        }
        unbind();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        destroy();

        createTexture();
    }

    public void bind() {
        GL43.glBindTexture(GL11.GL_TEXTURE_2D, id);
    }

    public void unbind() {
        GL43.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        GL11.glDeleteTextures(id);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

}