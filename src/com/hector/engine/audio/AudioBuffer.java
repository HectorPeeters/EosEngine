package com.hector.engine.audio;

import com.hector.engine.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioBuffer {

    private int bufferId;

    public AudioBuffer(AudioInputStream stream) {
        AudioFormat audioFormat = stream.getFormat();

        int format = getOpenAlFormat(audioFormat.getChannels(), audioFormat.getSampleSizeInBits());
        int samplerate = (int) audioFormat.getSampleRate();

        int bytesPerFrame = audioFormat.getFrameSize();
        int totalBytes = (int) (stream.getFrameLength() * bytesPerFrame);

        ByteBuffer data = BufferUtils.createByteBuffer(totalBytes);
        byte[] dataArray = new byte[totalBytes];

        try {
            int bytesRead = stream.read(dataArray, 0, totalBytes);
            data.clear();
            data.put(dataArray, 0, bytesRead);
            data.flip();

            this.bufferId = AudioSystem.generateBuffer(data, format, samplerate);

            stream.close();
            data.clear();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("Audio", "Couldn't read bytes from audio stream");
        }
    }

    private static int getOpenAlFormat(int channels, int bitsPerSample) {
        if (channels == 1) {
            return bitsPerSample == 8 ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
        } else {
            return bitsPerSample == 8 ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
        }
    }

    public int getBufferId() {
        return bufferId;
    }
}