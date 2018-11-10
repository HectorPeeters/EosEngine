package com.hector.engine.resource.resources;

import com.hector.engine.logging.Logger;
import com.hector.engine.resource.AbstractResourceLoader;
import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class TextureResource extends AbstractResource<Integer> {

    public TextureResource(String path) {
        super(path);
    }

    @Override
    public boolean load(AbstractResourceLoader resourceLoader) {

        try {
            PNGDecoder decoder = new PNGDecoder(resourceLoader.getInputStream(path));

            ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());

            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

            buffer.flip();

            int id = GL11.glGenTextures();

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            resource = id;

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("Resource", "Failed to load texture from " + path);
            return false;
        }


//        ByteBuffer image;
//
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            IntBuffer w = stack.mallocInt(1);
//            IntBuffer h = stack.mallocInt(1);
//            IntBuffer comp = stack.mallocInt(1);
//
//            byte[] imageData = readInputStream(resourceLoader.getInputStream(path));
//            if (imageData == null)
//                return false;
//
//            ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageData.length);
//            imageBuffer.put(imageData);
//            imageBuffer.flip();
//
//            image = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 0);
//
//            for (int i = 0; i < image.asCharBuffer().length(); i++) {
//                System.out.print((byte)image.asCharBuffer().get(i) + ", ");
//            }
//
//            if (image == null) {
//                Logger.err("Resource", "Failed to load image from path " + path);
//                return false;
//            }
//
//            int id = GL11.glGenTextures();
//            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//
//            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w.get(), h.get(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);
//
//            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
//
//            resource = id;
//        }
//
//        return true;
    }

    private byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int read;
        byte[] data = new byte[2048];

        try {

            while ((read = is.read(data, 0, data.length)) != -1)
                buffer.write(data, 0, read);

            buffer.flush();

            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.err("Resource", "Failed to convert input stream to byte array");
        }

        return null;
    }

}