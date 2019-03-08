package com.hector.engine.audio.components;

import com.hector.engine.entity.AbstractEntityComponent;

public class AudioSourceComponent extends AbstractEntityComponent {

    public String path;
    public float pitch;
    public float gain;
    public boolean looping;

}
