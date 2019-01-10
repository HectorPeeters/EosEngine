package com.hector.engine.audio;

import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.openal.*;

public class AudioSystem extends AbstractSystem {

    private long device;
    private long context;

    public AudioSystem() {
        super("audio", 3700);
    }

    @Override
    protected void init() {
        String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        device = ALC10.alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        context = ALC10.alcCreateContext(device, attributes);
        ALC10.alcMakeContextCurrent(context);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
    }

    @Override
    protected void reset() {

    }

    @Override
    protected void destroy() {
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }
}
