package com.hector.engine.graphics;

import com.hector.engine.EngineStateEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.graphics.events.WindowResizeEvent;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.logging.Logger;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class Display {

    private long window;
    private boolean closing = false;

    private int width, height;

    public boolean create(int width, int height) {
        long startTime = System.currentTimeMillis();

        this.width = width;
        this.height = height;

        if (!GLFW.glfwInit()) {
            Logger.err("Graphics", "Failed to init GLFW");
            return false;
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
//        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8);

        window = GLFW.glfwCreateWindow(width, height, "Engine", MemoryUtil.NULL, MemoryUtil.NULL);

        if (window == MemoryUtil.NULL) {
            Logger.err("Graphics", "Failed to create GLFW window");
            return false;
        }

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                GLFW.glfwSetWindowShouldClose(window, true);

            if (action == GLFW.GLFW_REPEAT)
                return;

            EventSystem.publish(new KeyEvent(key, action == GLFW.GLFW_PRESS));
        });

        GLFW.glfwSetWindowSizeCallback(window, (window, w, h) -> {
            EventSystem.publish(new WindowResizeEvent(width, height, w, h));

            this.width = w;
            this.height = h;

            GL11.glViewport(0, 0, w, h);
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

        GLFW.glfwSwapInterval(1);

        GLFW.glfwShowWindow(window);

        GL.createCapabilities();

        GL11.glViewport(0, 0, width, height);

        GL11.glClearColor(0.3f, 0.3f, 0.3f, 0.0f);


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

        Logger.info("Graphics", "Terminated GLFW");
    }

}
