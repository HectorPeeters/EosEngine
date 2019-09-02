package com.hector.engine.resource;

import java.io.InputStream;

public abstract class AbstractResourceLoader {

    public abstract String[] listFiles(String path);

    public abstract String loadText(String path);

    public abstract InputStream getInputStream(String path);

    public abstract boolean doesFileExist(String path);

    public abstract long getFileSize(String path);
}
