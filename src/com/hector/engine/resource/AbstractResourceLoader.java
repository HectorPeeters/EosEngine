package com.hector.engine.resource;

public abstract class AbstractResourceLoader {

    public abstract String[] listFiles(String path);

    public abstract String loadText(String path);

    public abstract boolean doesFileExist(String path);
}
