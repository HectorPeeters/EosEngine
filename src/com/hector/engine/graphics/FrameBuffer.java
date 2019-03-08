package com.hector.engine.graphics;

import com.hector.engine.logging.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import java.nio.ByteBuffer;

public class FrameBuffer {

    private int width, height;

    private int id;
    private int textureId;
    private int renderBufferId;

    FrameBuffer(int width, int height) {
        this.width = width;
        this.height = height;

        id = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        bind();

        attachTexture();
//        attachDepthBuffer();

        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
            Logger.err("Graphics", "Failed to create framebuffer");

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    private void attachTexture() {
        textureId = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB,
                GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL32.glFramebufferTexture2D(GL30.GL_DRAW_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureId, 0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

//        System.out.println("GL ERROR: " + GL11.glGetError());
    }

    private void attachDepthBuffer() {
        renderBufferId = GL30.glGenRenderbuffers();

        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBufferId);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, width, height);
//        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);

        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER,
                renderBufferId);
    }

    void bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
    }

    void unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void destroy() {
        GL30.glDeleteFramebuffers(id);
        Logger.info("Graphics", "Destroyed framebuffer");
    }

    int getTextureId() {
        return textureId;
    }
}
