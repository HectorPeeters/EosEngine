package com.hector.engine.utils;

public class UpdateTimer {

    private final double ns;
    private long lastTime;
    private double delta;

    private long timer;

    private int updates;
    private int frames;

    private boolean secondPassed = false;

    public UpdateTimer(float targetFPS) {
        ns = 1000000000.0 / targetFPS;
        lastTime = System.nanoTime();

        timer = System.currentTimeMillis();
    }

    public boolean shouldUpdateFPS() {
        long now = System.nanoTime();
        delta += (now - lastTime) / ns;
        lastTime = now;

        frames++;

        if (delta >= 1) {
            delta--;
            updates++;
            return true;
        }

        return false;
    }

    public boolean shouldUpdateSecond() {
        if (secondPassed) {
            updates = 0;
            frames = 0;
            secondPassed = false;
        }

        if (System.currentTimeMillis() - timer > 1000) {
            timer += 1000;
            secondPassed = true;
            return true;
        }

        return false;
    }

    public double getDelta() {
        return delta;
    }

    public int getUpdates() {
        return updates;
    }

    public int getFrames() {
        return frames;
    }
}
