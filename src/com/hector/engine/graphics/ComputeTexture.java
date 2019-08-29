package com.hector.engine.graphics;

import org.lwjgl.opengl.GL43;

import java.nio.ByteBuffer;

public class ComputeTexture {

    protected int id;
    private int width;
    private int height;

    private int format = GL43.GL_RGBA32F;
    private int mode = GL43.GL_WRITE_ONLY;

    public ComputeTexture(int width, int height) {
        this.width = width;
        this.height = height;
        this.id = GL43.glGenTextures();

        GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0, format, width, height, 0, GL43.GL_RGBA, GL43.GL_FLOAT, (ByteBuffer) null);
    }

    public ComputeTexture(int width, int height, int mode) {
        this.width = width;
        this.height = height;
        this.id = GL43.glGenTextures();
        this.mode = mode;

        GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0, format, width, height, 0, GL43.GL_RGBA, GL43.GL_FLOAT, (ByteBuffer) null);
    }

    public ComputeTexture(int width, int height, int mode, int format) {
        this.width = width;
        this.height = height;
        this.id = GL43.glGenTextures();
        this.mode = mode;
        this.format = format;

        GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0, format, width, height, 0, GL43.GL_RGBA, GL43.GL_FLOAT, (ByteBuffer) null);
    }

    public void bind(int unit) {
        GL43.glBindTexture(GL43.GL_TEXTURE_2D, id);

        GL43.glBindImageTexture(unit, id, 0, false, 0, mode, format);
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        if (this.format != format)
            GL43.glTexImage2D(GL43.GL_TEXTURE_2D, 0, format, width, height, 0, GL43.GL_RGBA, GL43.GL_FLOAT, (ByteBuffer) null);

        this.format = format;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getId() {
        return id;
    }

}
