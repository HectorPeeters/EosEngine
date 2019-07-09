package com.hector.engine.utils;

public class FloatRingBuffer {

    private int size;

    private float[] data;

    private int pointer;

    public FloatRingBuffer(int size) {
        this.size = size;
        this.data = new float[size];
        this.pointer = 0;
    }

    public void add(float value) {
        data[pointer] = value;
        pointer = (pointer + 1) % size;
    }

    public float[] getAsArray() {
        return data;
    }

    public int getSize() {
        return size;
    }
}
