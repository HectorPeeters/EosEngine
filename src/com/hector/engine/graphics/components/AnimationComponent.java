package com.hector.engine.graphics.components;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.graphics.Animation;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.AnimationResource;

public class AnimationComponent extends AbstractEntityComponent {

    private Animation animation;

    private float currentFrameTime = 0;
    private int currentFrame = 0;

    private boolean playOnce = false;
    private boolean isPlaying = true;

    private boolean flipped = false;

    private String path;

    public AnimationComponent(String path) {
        this.path = path;
    }

    @Override
    public void init() {
        AnimationResource resource = ResourceManager.getResource(path);
        if (resource == null)
            return;

        this.isPlaying = true;
        this.animation = resource.getResource();
    }

    public void update(float delta) {
        if (!isPlaying)
            return;

        currentFrameTime += delta;

        if (currentFrameTime >= 1f / animation.getFps()) {
            currentFrame++;

            if (currentFrame >= animation.getTotalFrames()) {
                if (playOnce) {
                    isPlaying = false;
                    currentFrame = animation.getTotalFrames() - 1;
                } else {
                    currentFrame = 0;
                }
            }

            currentFrameTime -= 1f / animation.getFps();
        }
    }

    public void setAnimation(Animation animation) {
        if (this.animation == animation)
            return;

        this.animation = animation;
        this.currentFrame = 0;
        this.currentFrameTime = 0;
    }

    public void play(boolean once) {
        isPlaying = true;

        playOnce = once;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlayOnce(boolean playOnce) {
        this.playOnce = playOnce;
    }

    public void pauze() {
        isPlaying = false;
    }

    public void stop() {
        isPlaying = false;
        currentFrameTime = 0;
        currentFrame = 0;
    }

    public void setFrame(int frame) {
        this.currentFrame = frame;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public int getTextureId() {
        return animation.getTexture().getId();
    }

    public int getFramesWide() {
        return animation.getFramesWide();
    }

    public int getFramesHigh() {
        return animation.getFramesHigh();
    }

    public int getFrameIndex() {
        return currentFrame;
    }

}
