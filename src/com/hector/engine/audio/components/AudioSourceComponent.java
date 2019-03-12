package com.hector.engine.audio.components;

import com.hector.engine.audio.AudioBuffer;
import com.hector.engine.entity.AbstractEntityComponent;
import org.lwjgl.openal.AL10;

//TODO: make class variables private with getters and setters
public class AudioSourceComponent extends AbstractEntityComponent {

    public int id;
    public String path;
    public float pitch = 1;
    public float gain = 1;
    public boolean looping = false;
    public boolean global = false;

    public boolean playing;
    public boolean paused;

    public AudioBuffer buffer;

    public void play() {
        if (!playing || paused)
            AL10.alSourcePlay(id);

        playing = true;
        paused = false;
    }

    public void stop() {
        if (playing)
            AL10.alSourceStop(id);

        playing = false;
    }

    public void pause() {
        if (!paused)
            AL10.alSourcePause(id);

        paused = true;
        playing = false;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
        AL10.alSourcef(id, AL10.AL_PITCH, pitch);
    }

    public void setGain(float gain) {
        this.gain = gain;
        AL10.alSourcef(id, AL10.AL_GAIN, gain);
    }

    public boolean isPlaying() {
        return playing;
    }
}
