package com.hector.engine.graphics;

import java.util.ArrayList;
import java.util.List;

public class MeshBuilder {

    private List<Float> vertices;
    private List<Float> textureCoords;

    public MeshBuilder() {
        vertices = new ArrayList<>();
        textureCoords = new ArrayList<>();
    }

    public void addVertex(float vx, float vy, float tx, float ty) {
        vertices.add(vx);
        vertices.add(vy);

        textureCoords.add(tx);
        textureCoords.add(ty);
    }

    public Mesh build() {
        Mesh mesh = new Mesh(toArray(vertices), toArray(textureCoords));
        vertices.clear();
        textureCoords.clear();

        return mesh;
    }

    private float[] toArray(List<Float> data) {
        float[] result = new float[data.size()];
        for (int i = 0; i < result.length; i++)
            result[i] = data.get(i);

        return result;
    }

}
