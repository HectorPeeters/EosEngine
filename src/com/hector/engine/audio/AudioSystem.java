package com.hector.engine.audio;

import com.hector.engine.audio.components.AudioListenerComponent;
import com.hector.engine.audio.components.AudioSourceComponent;
import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.Entity;
import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.event.Handler;
import com.hector.engine.logging.Logger;
import com.hector.engine.maths.Vector2f;
import com.hector.engine.physics.components.RigidbodyComponent;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.AudioResource;
import com.hector.engine.systems.AbstractSystem;
import org.lwjgl.openal.*;

import java.util.ArrayList;
import java.util.List;

public class AudioSystem extends AbstractSystem {

    private long device;
    private long context;

    private AudioListenerInstance audioListener;

    private List<AudioSourceInstance> sourceComponents = new ArrayList<>();

    private List<Integer> audioBuffers = new ArrayList<>();
    private List<Integer> audioSources = new ArrayList<>();

    public AudioSystem() {
        super("audio", 3700);
    }

    @Override
    protected void init() {
        String defaultDeviceName = ALC10.alcGetString(0, ALC10.ALC_DEFAULT_DEVICE_SPECIFIER);
        device = ALC10.alcOpenDevice(defaultDeviceName);

        int[] attributes = {0};
        context = ALC10.alcCreateContext(device, attributes);
        if (!ALC10.alcMakeContextCurrent(context)) {
            Logger.err("Audio", "Failed to make OpenAL context current");
            return;
        }

        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (alCapabilities.OpenAL11)
            Logger.debug("Audio", "OpenAL 11 supported");
        else if (alCapabilities.OpenAL10)
            Logger.debug("Audio", "OpenAL 10 supported");


        int error = ALC10.alcGetError(device);
        if (error != ALC10.ALC_NO_ERROR) {
            Logger.err("Audio", "OpenAL Error: " + error);
        }
    }

    private int generateBuffer(WaveData waveData) {
        int bufferId = AL10.alGenBuffers();

        AL10.alBufferData(bufferId, waveData.format, waveData.data, waveData.samplerate);
        waveData.dispose();

        audioBuffers.add(bufferId);

        return bufferId;
    }

    private int generateSource(Vector2f position, Vector2f velocity, float pitch, float gain, boolean looping) {
        int id = AL10.alGenSources();

        AL10.alSourcef(id, AL10.AL_PITCH, pitch);
        AL10.alSourcef(id, AL10.AL_GAIN, gain);
        AL10.alSource3f(id, AL10.AL_POSITION, position.x, position.y, 0);
        AL10.alSource3f(id, AL10.AL_VELOCITY, velocity.x, velocity.y, 0);
        AL10.alSourcei(id, AL10.AL_LOOPING, looping ? 1 : 0);

        audioSources.add(id);

        Logger.debug("Audio", "Created audio source " + id);

        return id;
    }

    @Override
    public void postUpdate(float delta) {
        int error = AL10.alGetError();
        if (error != AL10.AL_NO_ERROR)
            Logger.err("Audio", "OpenAL error: " + error);

        for (AudioSourceInstance inst : sourceComponents) {
            if (inst.id == -1) {
                AudioResource resource = ResourceManager.getResource(inst.sourceComponent.path);
                int buffer = generateBuffer(resource.getResource());

                inst.id = generateSource(inst.entity.getPosition(), new Vector2f(0, 0), inst.sourceComponent.pitch, inst.sourceComponent.gain, inst.sourceComponent.looping);


                AL10.alSourcei(inst.id, AL10.AL_BUFFER, buffer);
                AL10.alSourcePlay(inst.id);
            }

            AL10.alSource3f(inst.id, AL10.AL_POSITION, inst.entity.getPosition().x, inst.entity.getPosition().y, 0);
            if (inst.rb != null)
                AL10.alSource3f(inst.id, AL10.AL_VELOCITY, inst.rb.getVelocity().x, inst.rb.getVelocity().y, 0);
        }

        AL10.alListener3f(AL10.AL_POSITION, audioListener.entity.getPosition().x, audioListener.entity.getPosition().y, 0);

        if (audioListener.rb != null)
            AL10.alListener3f(AL10.AL_VELOCITY, audioListener.rb.getVelocity().x, audioListener.rb.getVelocity().y, 0);

        AL10.alListenerf(AL10.AL_GAIN, audioListener.listenerComponent.gain);
    }

    @Handler
    private void handleAddComponentEvent(AddEntityComponentEvent event) {
        for (AbstractEntityComponent component : event.components) {
            if (component instanceof AudioListenerComponent) {
                if (audioListener != null)
                    Logger.warn("Audio", "Audio listener already set");

                Logger.debug("Audio", "Set audio listener for scene");

                audioListener = new AudioListenerInstance();
                audioListener.entity = component.getParent();

                if (component.getParent().hasComponent(RigidbodyComponent.class)) {
                    audioListener.rb = component.getParent().getComponent(RigidbodyComponent.class);
                }

                audioListener.listenerComponent = (AudioListenerComponent) component;
            } else if (component instanceof AudioSourceComponent) {

                AudioSourceComponent sourceComponent = (AudioSourceComponent) component;

                AudioSourceInstance inst = new AudioSourceInstance();
                inst.entity = sourceComponent.getParent();

                if (sourceComponent.getParent().hasComponent(RigidbodyComponent.class)) {
                    inst.rb = sourceComponent.getParent().getComponent(RigidbodyComponent.class);
                }

                inst.sourceComponent = sourceComponent;
                inst.id = -1;

                sourceComponents.add(inst);
            }
        }
    }

    @Override
    protected void reset() {

    }

    @Override
    protected void destroy() {
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);

        for (int buffer : audioBuffers)
            AL10.alDeleteBuffers(buffer);

        for (int source : audioSources)
            AL10.alDeleteSources(source);
    }

    private static class AudioSourceInstance {
        int id;
        Entity entity;
        RigidbodyComponent rb;
        AudioSourceComponent sourceComponent;
    }

    private static class AudioListenerInstance {
        Entity entity;
        RigidbodyComponent rb;
        AudioListenerComponent listenerComponent;
    }
}
