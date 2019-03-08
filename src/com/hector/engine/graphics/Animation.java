package com.hector.engine.graphics;

public class Animation {

    private Texture texture;
    private int framesWide;
    private int framesHigh;
    private float fps;

    public Animation(Texture texture, int framesWide, int framesHigh, float fps) {
        this.texture = texture;
        this.framesWide = framesWide;
        this.framesHigh = framesHigh;
        this.fps = fps;
    }

    public Texture getTexture() {
        return texture;
    }

    public int getFramesWide() {
        return framesWide;
    }

    public int getFramesHigh() {
        return framesHigh;
    }

    public float getFps() {
        return fps;
    }

    public int getTotalFrames() {
        return framesHigh * framesWide;
    }
}
