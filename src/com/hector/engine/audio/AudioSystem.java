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

import java.util.HashMap;
import java.util.Map;

public class AudioSystem extends AbstractSystem {

    private long device;
    private long context;

    private AudioListenerComponent audioListenerComponent;
    private Map<AudioSourceComponent, Integer> sourceComponents = new HashMap<>();

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

        int error = ALC10.alcGetError(device);
        if (error != ALC10.ALC_NO_ERROR) {
            Logger.err("Audio", "OpenAL Error: " + error);
        }
    }

    private int generateBuffer(WaveData waveData) {
        int bufferId = AL10.alGenBuffers();

        AL10.alBufferData(bufferId, waveData.format, waveData.data, waveData.samplerate);
        waveData.dispose();

        return bufferId;
    }

    private int generateSource(Vector2f position, Vector2f velocity, float pitch, float gain, boolean looping) {
        int id = AL10.alGenSources();

        AL10.alSourcef(id, AL10.AL_PITCH, pitch);
        AL10.alSourcef(id, AL10.AL_GAIN, gain);
        AL10.alSource3f(id, AL10.AL_POSITION, position.x, position.y, 0);
        AL10.alSource3f(id, AL10.AL_VELOCITY, velocity.x, velocity.y, 0);
        AL10.alSourcei(id, AL10.AL_LOOPING, looping ? 1 : 0);

        Logger.debug("Audio", "Created audio source " + id);

        return id;
    }

    @Override
    public void postUpdate(float delta) {
        for (AudioSourceComponent comp : sourceComponents.keySet()) {
            int sourceId = sourceComponents.get(comp);

            if (sourceId == -1) {
                AudioResource resource = ResourceManager.<AudioResource>getResource(comp.path);
                int buffer = generateBuffer(resource.getResource());

                sourceComponents.put(comp, generateSource(comp.getParent().getPosition(), new Vector2f(0, 0), comp.pitch, comp.gain, comp.looping));

                AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
                AL10.alSourcePlay(sourceId);
            }

            if (comp.getParent().hasComponent(RigidbodyComponent.class)) {
                //TODO: Maybe cache this component
                RigidbodyComponent rb = comp.getParent().getComponent(RigidbodyComponent.class);

                AL10.alSource3f(sourceId, AL10.AL_POSITION, rb.getPosition().x, rb.getPosition().y, 0);
                AL10.alSource3f(sourceId, AL10.AL_VELOCITY, rb.getVelocity().x, rb.getVelocity().y, 0);
            }
        }
    }

    @Handler
    private void handleAddComponentEvent(AddEntityComponentEvent event) {
        for (AbstractEntityComponent component : event.components) {
            if (component instanceof AudioListenerComponent) {
                if (audioListenerComponent != null)
                    Logger.warn("Audio", "Audio listener already set");

                Logger.debug("Audio", "Set audio listener for scene");
                audioListenerComponent = (AudioListenerComponent) component;

            } else if (component instanceof AudioSourceComponent) {
                AudioSourceComponent sourceComponent = (AudioSourceComponent) component;
                sourceComponents.put(sourceComponent, -1);
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

        sourceComponents.clear();
    }
}
