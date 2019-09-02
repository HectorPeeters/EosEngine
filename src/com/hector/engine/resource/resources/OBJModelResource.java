package com.hector.engine.resource.resources;

import com.hector.engine.graphics.Model;
import com.hector.engine.resource.AbstractResourceLoader;
import com.hector.engine.resource.ResourceManager;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

//TODO: Add error checking
public class OBJModelResource extends AbstractResource<Model> {

    public OBJModelResource(String path) {
        super(path);
    }

    @Override
    public boolean load(AbstractResourceLoader resourceLoader) {
        TextResource resource = ResourceManager.getResource(path);

        if (resource == null)
            return false;

        String text = resource.getResource();

        ResourceManager.unloadResource(resource);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textureCoords = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();

        for (String line : text.split("\n")) {

            if (line.startsWith("v ")) {
                line = line.replace("v ", "");
                String[] lineData = line.split(" ");

                float x = Float.parseFloat(lineData[0]);
                float y = Float.parseFloat(lineData[1]);
                float z = Float.parseFloat(lineData[2]);

                vertices.add(new Vector3f(x, y, z));
            } else if (line.startsWith("vt ")) {
                line = line.replace("vt ", "");
                String[] lineData = line.split(" ");

                float u = Float.parseFloat(lineData[0]);
                float v = Float.parseFloat(lineData[1]);

                textureCoords.add(new Vector2f(u, v));
            } else if (line.startsWith("vn ")) {
                line = line.replace("vn ", "");
                String[] lineData = line.split(" ");

                float x = Float.parseFloat(lineData[0]);
                float y = Float.parseFloat(lineData[1]);
                float z = Float.parseFloat(lineData[2]);

                normals.add(new Vector3f(x, y, z));
            } else if (line.startsWith("f ")) {
                line = line.replace("f ", "");

                String[] lineData = line.split(" ");
                String[] indices0 = lineData[0].split("/");
                String[] indices1 = lineData[1].split("/");
                String[] indices2 = lineData[2].split("/");


            }
        }

        Model model;

        return false;
    }
}
