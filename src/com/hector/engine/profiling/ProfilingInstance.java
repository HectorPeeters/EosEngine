package com.hector.engine.profiling;

public class ProfilingInstance {

    private String name;
    private int numIterations;
    private long totalTime;
    private long previousStart;

    public ProfilingInstance(String name) {
        this.name = name;
        this.numIterations = 0;
        this.totalTime = 0;
    }

    public void start() {
        previousStart = System.nanoTime();
    }

    public void stop() {
        long time = System.nanoTime() - previousStart;
        totalTime += time;
        numIterations++;
    }

    public String getName() {
        return name;
    }

    public double timePerIterationMS() {
        return getTotalTimeMS() / numIterations;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public double getTotalTimeMS() {
        return totalTime / 1000000.0;
    }
}
