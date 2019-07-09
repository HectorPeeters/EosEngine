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

        TextureResource textureResource = ResourceManager.getResource(path.replace(".anim", ""));

        Texture texture = textureResource.getResource();

        if (texture == null)
            return false;

        int framesWide = 4;
        int framesHigh = 1;
        float fps = 4;

        if (ResourceManager.doesResourceExist(path)) {
            String assetFile = resourceLoader.loadText(path);

            for (String s : assetFile.split("\n")) {
                String[] data = s.split("=");

                switch (data[0]) {
                    case "framesWide":
                        framesWide = Integer.parseInt(data[1]);
                        break;

                    case "framesHigh":
                        framesHigh = Integer.parseInt(data[1]);
                        break;

                    case "fps":
                        fps = Float.parseFloat(data[1]);
                        break;

                    default:
                        Logger.warn("Resource", "Corrupt line in " + path);
                        break;
                }
            }

        } else {
            Logger.warn("Resource", path + " not found. Loading defaults");
        }

        resource = new Animation(texture, framesWide, framesHigh, fps);

        return true;
    }

}
