package com.hector.engine.resource.resources;

import com.hector.engine.graphics.Animation;
import com.hector.engine.graphics.Texture;
import com.hector.engine.logging.Logger;
import com.hector.engine.resource.AbstractResourceLoader;
import com.hector.engine.resource.ResourceManager;

public class AnimationResource extends AbstractResource<Animation> {

    public AnimationResource(String path) {
        super(path);
    }

    @Override
    public boolean load(AbstractResourceLoader resourceLoader) {

        Texture texture = ResourceManager.<TextureResource>getResource(path.replace(".anim", "")).getResource();

        if (texture == null)
            return false;

        int framesWide = 4;
        int framesHigh = 1;
        float fps = 4;

        if (ResourceManager.doesResourceExist(path)) {
            String assetFile = resourceLoader.loadText(path);

            for (String s : assetFile.split("\n")) {
                String[] data = s.split("=");

                if (data[0].equals("framesWide")) {
                    framesWide = Integer.parseInt(data[1]);
                } else if (data[0].equals("framesHigh")) {
                    framesHigh = Integer.parseInt(data[1]);
                } else if (data[0].equals("fps")) {
                    fps = Float.parseFloat(data[1]);
                } else {
                    Logger.warn("Resource", "Corrupt line in " + path);
                }
            }

        } else {
            Logger.warn("Resource", path + " not found. Loading defaults");
        }

        resource = new Animation(texture, framesWide, framesHigh, fps);

        return true;
    }

}
