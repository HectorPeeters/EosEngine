package com.hector.engine.resource.resources;

import com.hector.engine.audio.AudioBuffer;
import com.hector.engine.resource.AbstractResourceLoader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioResource extends AbstractResource<AudioBuffer> {

    public AudioResource(String path) {
        super(path);
    }

    @Override
    public boolean load(AbstractResourceLoader resourceLoader) {
        InputStream is = resourceLoader.getInputStream(path);
        if (is == null)
            return false;

        BufferedInputStream bufferedInput = new BufferedInputStream(is);
        AudioInputStream audioStream;
        try {
            audioStream = AudioSystem.getAudioInputStream(bufferedInput);
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            return false;
        }

        resource = new AudioBuffer(audioStream);

        return true;
    }
}
