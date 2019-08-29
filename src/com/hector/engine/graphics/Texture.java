package com.hector.engine.graphics;

import org.lwjgl.opengl.GL11;

public class Texture {

    protected int id;
    private int width;
    private int height;

    private int type = GL11.GL_TEXTURE_2D;

    public Texture(int width, int height) {
        this.width = width;
        this.height = height;
        this.id = GL11.glGenTextures();
    }

    public Texture(int width, int height, int type) {
        this.width = width;
        this.height = height;
        this.id = GL11.glGenTextures();
        this.type = type;
    }

    public void bind() {
        GL11.glBindTexture(type, id);
    }

    public void unbind() {
        GL11.glBindTexture(type, 0);
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getType() {
        return type;
    }

}
