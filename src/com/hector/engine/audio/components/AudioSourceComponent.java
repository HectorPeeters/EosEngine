package com.hector.engine.audio.components;

import com.hector.engine.entity.AbstractEntityComponent;

//TODO: make class variables private with getters and setters
public class AudioSourceComponent extends AbstractEntityComponent {

    public String path;
    public float pitch = 1;
    public float gain = 1;
    public boolean looping = false;
    public boolean global = false;
}
