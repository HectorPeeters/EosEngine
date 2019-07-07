package com.hector.engine.graphics;

import com.hector.engine.EngineStateEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.graphics.events.WindowResizeEvent;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.input.events.MouseButtonEvent;
import com.hector.engine.input.events.MouseMoveEvent;
import com.hector.engine.logging.Logger;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class Display {

    private long window;
    private boolean closing = false;

    public boolean create(int width, int height, int samples) {
        if (!GLFW.glfwInit()) {
            Logger.err("Graphics", "Failed to init GLFW");
            return false;
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        if (samples != 0)
            GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, samples);

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

        GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            EventSystem.publish(new MouseButtonEvent(button, action == GLFW.GLFW_PRESS));
        });

        GLFW.glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            //Convert to normalized coordinates
            EventSystem.publish(new MouseMoveEvent((xpos / width - 0.5f) * 2f, (ypos / height - 0.5f) * 2f));
        });

        GLFW.glfwSetWindowSizeCallback(window, (window, w, h) -> {
            EventSystem.publish(new WindowResizeEvent(w, h));

            GL11.glViewport(0, 0, w, h);
        });

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode videoMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            if (videoMode == null) {
                Logger.err("Graphics", "Failed to get video mode");
                return false;
            }

            GLFW.glfwSetWindowPos(
                    window,
                    (videoMode.width() - pWidth.get(0)) / 2,
                    (videoMode.height() - pHeight.get(0)) / 2
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

    public long getId() {
        return window;
    }

}
