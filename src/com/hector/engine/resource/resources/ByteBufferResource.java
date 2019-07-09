package com.hector.engine.resource.resources;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.AbstractResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferResource extends AbstractResource<ByteBuffer> {

    public ByteBufferResource(String path) {
        super(path);
    }

    @Override
    public boolean load(AbstractResourceLoader resourceLoader) {
        InputStream is = resourceLoader.getInputStream(path);
        if (is == null)
            return false;

        resource = ByteBuffer.allocate((int) resourceLoader.getFileSize(path));
        try {
            while (is.available() > 0) {
                byte b = (byte) is.read();
                resource.put(b);
            }

            is.close();
        } catch (IOException e) {
            Logger.err("Resource", "Failed to read inputstream of file " + path);
            return false;
        }

        resource.flip();

        return true;
    }
}
