package com.hector.engine.audio;

import com.hector.engine.audio.components.AudioListenerComponent;
import com.hector.engine.audio.components.AudioSourceComponent;
import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.event.Handler;
import com.hector.engine.logging.Logger;
import com.hector.engine.maths.Vector2f;
import com.hector.engine.physics.components.RigidbodyComponent;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.AudioResource;
import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.openal.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AudioSystem extends AbstractSystem {

    private long alDevice;
    private long alContext;

    private static List<Integer> audioTracks = new ArrayList<>();

    private List<AudioSourceInstance> audioSourceInstances = new ArrayList<>();
    private AudioListenerInstance audioListenerInstance;

    public AudioSystem() {
        super("audio", 3700);
    }

    @Override
    protected void init() {
        String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        alDevice = ALC10.alcOpenDevice(defaultDeviceName);

        alContext = ALC10.alcCreateContext(alDevice, new int[]{0});
        if (!ALC10.alcMakeContextCurrent(alContext)) {
            Logger.err("Audio", "Failed to make OpenAL context current");
            return;
        }

        ALCCapabilities alcCapabilities = ALC.createCapabilities(alDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        int error = ALC10.alcGetError(alDevice);
        if (error == ALC10.ALC_NO_ERROR) {
            Logger.info("Audio", "Initialized OpenAL with device \'" + alDevice + "\' and " +
                    (alCapabilities.OpenAL11 ? "OpenAL11 support" :
                            (alCapabilities.OpenAL10 ? "OpenAL10 support" : "?no support?")));
        } else {
            Logger.err("Audio", "ALC Error " + error);
        }
    }

    @Override
    public void postUpdate(float delta) {
        //Update audio sources
        for (AudioSourceInstance inst : audioSourceInstances) {
            if (inst.sourceComponent.global) {
                AL10.alSource3f(inst.sourceComponent.id, AL10.AL_POSITION, audioListenerInstance.listenerComponent.getParent().getPosition().x, audioListenerInstance.listenerComponent.getParent().getPosition().y, 0);
            } else {
                AL10.alSource3f(inst.sourceComponent.id, AL10.AL_POSITION, inst.sourceComponent.getParent().getPosition().x, inst.sourceComponent.getParent().getPosition().y, 0);
            }

            Vector2f velocityVector = new Vector2f(0, 0);
            if (inst.rb != null)
                velocityVector = inst.rb.getVelocity();
            AL10.alSource3f(inst.sourceComponent.id, AL10.AL_VELOCITY, velocityVector.x, velocityVector.y, 0);

            AL10.alSourcei(inst.sourceComponent.id, AL10.AL_LOOPING, inst.sourceComponent.looping ? 1 : 0);
        }

        //Update audio listener
        if (audioListenerInstance != null) {
            Vector2f listenerPosition = audioListenerInstance.listenerComponent.getParent().getPosition();
            AL10.alListener3f(AL10.AL_POSITION, listenerPosition.x, listenerPosition.y, 0);

            if (audioListenerInstance.rb != null)
                AL10.alListener3f(AL10.AL_VELOCITY, audioListenerInstance.rb.getVelocity().x, audioListenerInstance.rb.getVelocity().y, 0);

            AL10.alListenerf(AL10.AL_GAIN, audioListenerInstance.listenerComponent.gain);
        }

        //Error handling
        int error = AL10.alGetError();
        if (error != AL10.AL_NO_ERROR)
            Logger.err("Audio", "OpenAL error: " + error);
    }

    public static int generateBuffer(ByteBuffer data, int format, int sampleRate) {
        int bufferId = AL10.alGenBuffers();

        AL10.alBufferData(bufferId, format, data, sampleRate);

        audioTracks.add(bufferId);

        Logger.debug("Audio", "Created audio buffer with id " + bufferId);

        return bufferId;
    }

    @Handler
    private void handleAddComponentEvent(AddEntityComponentEvent event) {
        for (AbstractEntityComponent component : event.components) {
            if (component instanceof AudioSourceComponent) {
                addAudioSource((AudioSourceComponent) component);
            } else if (component instanceof AudioListenerComponent) {
                addAudioListener((AudioListenerComponent) component);
            }
        }
    }

    private void addAudioSource(AudioSourceComponent audioSourceComponent) {
        AudioSourceInstance inst = new AudioSourceInstance();

        inst.sourceComponent = audioSourceComponent;
        inst.sourceComponent.id = AL10.alGenSources();

        AudioResource resource = ResourceManager.getResource(inst.sourceComponent.path);

        inst.sourceComponent.buffer = resource.getResource();

        AL10.alSourcei(inst.sourceComponent.id, AL10.AL_BUFFER, inst.sourceComponent.buffer.getBufferId());

        if (audioSourceComponent.getParent().hasComponent(RigidbodyComponent.class)) {
            inst.rb = audioSourceComponent.getParent().getComponent(RigidbodyComponent.class);
        }

        audioSourceInstances.add(inst);
    }

    private void addAudioListener(AudioListenerComponent audioListenerComponent) {
        AudioListenerInstance inst = new AudioListenerInstance();

        inst.listenerComponent = audioListenerComponent;

        if (audioListenerComponent.getParent().hasComponent(RigidbodyComponent.class)) {
            inst.rb = audioListenerComponent.getParent().getComponent(RigidbodyComponent.class);
        }

        if (audioListenerInstance != null)
            Logger.warn("Audio", "Audio listener has already been assigned");

        audioListenerInstance = inst;
    }

    @Override
    protected void destroy() {
        for (int track : audioTracks) {
            AL10.alDeleteBuffers(track);
        }

        ALC10.alcDestroyContext(alContext);
        ALC10.alcCloseDevice(alDevice);

        for (AudioSourceInstance inst : audioSourceInstances) {
            AL10.alDeleteSources(inst.sourceComponent.id);
        }

        Logger.info("Audio", "Closed OpenAL audio device");
    }

    private static class AudioSourceInstance {
        public RigidbodyComponent rb;
        public AudioSourceComponent sourceComponent;
    }

    private static class AudioListenerInstance {
        public RigidbodyComponent rb;
        public AudioListenerComponent listenerComponent;
    }

}
