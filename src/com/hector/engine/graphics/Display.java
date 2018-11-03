package com.hector.engine.graphics;

import com.hector.engine.event.EventSystem;
import com.hector.engine.EngineStateEvent;
import com.hector.engine.graphics.events.WindowResizeEvent;
import com.hector.engine.logging.Logger;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class Display {

    private long window;
    private boolean closing = false;

    private int width, height;

    public boolean create(int width, int height) {
        this.width = width;
        this.height = height;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            Logger.err("Graphics", "Failed to init GLFW");
            return false;
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

        window = GLFW.glfwCreateWindow(width, height, "Engine", MemoryUtil.NULL, MemoryUtil.NULL);

        if (window == MemoryUtil.NULL) {
            Logger.err("Graphics", "Failed to create GLFW window");
            return false;
        }

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                GLFW.glfwSetWindowShouldClose(window, true);
        });

        GLFW.glfwSetWindowSizeCallback(window, (window, w, h) -> {
            EventSystem.publish(new WindowResizeEvent(width, height, w, h));

            this.width = w;
            this.height = h;
        });

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            if (vidmode == null) {
                Logger.err("Graphics", "Failed to get vidmode");
                return false;
            }

            GLFW.glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        GLFW.glfwMakeContextCurrent(window);

        GLFW.glfwSwapInterval(0);

        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        GL11.glClearColor(0.1f, 0.1f, 0.1f, 0.0f);

        return true;
    }

    public void update() {
        if (GLFW.glfwWindowShouldClose(window)) {
            if (!closing)
                EventSystem.publish(new EngineStateEvent(EngineStateEvent.EngineState.STOP));

            closing = true;
            return;
        }

        GLFW.glfwSwapBuffers(window);

        GLFW.glfwPollEvents();
    }

    public void destroy() {
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);

        GLFW.glfwTerminate();

        GLFW.glfwSetErrorCallback(null).free();

        Logger.info("Graphics", "Terminated GLFW");
    }

}
