package com.hector.engine.graphics;

import org.lwjgl.opengl.GL20;

public class CubeMap {

    private int id;

    public CubeMap() {
        id = GL20.glGenTextures();
        GL20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, id);
    }

}
