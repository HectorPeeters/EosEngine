package com.hector.engine.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

public class Mesh {

    private int vaoId;
    private int vertexCount;

    private List<Integer> vboIds = new ArrayList<>();
    private List<Integer> attribArray = new ArrayList<>();

    public Mesh(float[] vertices, float[] textureCoords) {
        this.vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        this.vertexCount = vertices.length;

        attachVbo(vertices, 2, 0);
        attachVbo(textureCoords, 2, 1);
    }

    private void attachVbo(float[] data, int dimension, int attribLocation) {
        int vboId = GL30.glGenBuffers();
        vboIds.add(vboId);

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboId);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, data, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(attribLocation, dimension, GL11.GL_FLOAT, false, 0, 0);

        attribArray.add(attribLocation);
    }

    public void bind() {
        GL30.glBindVertexArray(vaoId);
    }

    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void enableVertexAttribArrays() {
        for (int vaa : attribArray)
            GL20.glEnableVertexAttribArray(vaa);
    }

    public void disableVertexAttribArrays() {
        for (int vaa : attribArray)
            GL20.glDisableVertexAttribArray(vaa);
    }

    public void destroy() {
        for (int vbo : vboIds)
            GL30.glDeleteBuffers(vbo);

        GL30.glDeleteVertexArrays(vaoId);
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

}
