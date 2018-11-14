package com.hector.engine.graphics.components;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.graphics.Texture;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextureResource;

public class AnimationComponent extends AbstractEntityComponent {

    private String texturePath;

    public Texture texture;

    private int framesWide;
    private int framesHigh;
    private float fps;

    private float currentFrameTime;
    private int currentFrame = 0;

    private boolean playOnce = false;
    private boolean isPlaying = false;

    private boolean flipped = false;

    public AnimationComponent(String texture, int framesWide, int framesHigh) {
        this.texturePath = texture;
        this.framesWide = framesWide;
        this.framesHigh = framesHigh;
        this.fps = 4;
    }

    @Override
    public void init() {
        TextureResource resource = ResourceManager.getResource(texturePath);
        if (resource == null)
            return;

        this.texture = resource.getResource();
    }

    public void advanceAnimation(float delta) {
        if (!isPlaying)
            return;

        currentFrameTime += delta;

        if (currentFrameTime >= 1f / fps) {
            currentFrame++;

            if (currentFrame >= framesHigh * framesWide) {
                if (playOnce) {
                    isPlaying = false;
                    currentFrame = framesHigh * framesWide - 1;
                } else {
                    currentFrame = 0;
                }
            }

            currentFrameTime -= 1f / fps;
        }
    }

    public void play(boolean once) {
        isPlaying = true;

        playOnce = once;
    }

    public void stop() {
        isPlaying = false;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public int getTextureId() {
        return texture.getId();
    }

    public int getFramesWide() {
        return framesWide;
    }

    public int getFramesHigh() {
        return framesHigh;
    }

    public int getFrameIndex() {
        return currentFrame;
    }
}
