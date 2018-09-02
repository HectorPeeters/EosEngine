package com.hector.engine;

public class UpdateTimer {

    private final double ns;
    private long lastTime;
    private double delta;

    private long timer;

    public UpdateTimer(float targetFPS) {
        ns = 1000000000.0 / targetFPS;
        lastTime = System.nanoTime();

        timer = System.currentTimeMillis();
    }

    public boolean shouldUpdateFPS() {
        long now = System.nanoTime();
        delta += (now - lastTime) / ns;
        lastTime = now;

        if (delta >= 1) {
            delta--;
            return true;
        }

        return false;
    }

    public boolean shouldUpdateSecond() {
        if (System.currentTimeMillis() - timer > 1000) {
            timer += 1000;
            return true;
        }

        return false;
    }

    public double getDelta() {
        return delta;
    }
}
