package com.hector.engine.graphics;

import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

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

    public void setData(int level, int internalformat, int format, int dataType, ByteBuffer data) {
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, level, internalformat, width, height, 0, format, dataType, data);
    }

    public void setFilter(int min, int mag) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, min);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mag);
    }

    public void destroy() {
        GL11.glDeleteTextures(id);
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
