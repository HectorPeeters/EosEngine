package com.hector.engine.resource.resources;

import com.hector.engine.resource.AbstractResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.BufferUtils.createByteBuffer;

public class NuklearFontResource extends AbstractResource<ByteBuffer> {

    private static final int BUFFER_SIZE = 512 * 1024;

    public NuklearFontResource(String path) {
        super(path);
    }

    @Override
    public boolean load(AbstractResourceLoader resourceLoader) {
        try (
                InputStream source = resourceLoader.getInputStream(path);
                ReadableByteChannel rbc = Channels.newChannel(source)
        ) {
            resource = createByteBuffer(BUFFER_SIZE);

            while (true) {
                int bytes = rbc.read(resource);
                if (bytes == -1) {
                    break;
                }
                if (resource.remaining() == 0) {
                    resource = resizeBuffer(resource, resource.capacity() * 3 / 2); // 50%
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        }

        resource.flip();
        return true;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}
