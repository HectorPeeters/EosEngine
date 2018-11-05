package com.hector.engine.resource.resources;

import java.io.InputStream;

public class StreamResource extends AbstractResource {

    private InputStream text;

    public StreamResource(String path, InputStream text) {
        super(path, true);
        this.text = text;
    }

    public InputStream getStream() {
        return text;
    }
}
