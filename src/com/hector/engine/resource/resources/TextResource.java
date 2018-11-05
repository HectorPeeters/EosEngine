package com.hector.engine.resource.resources;

public class TextResource extends AbstractResource {

    private String text;

    public TextResource(String path, String text) {
        super(path, true);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
