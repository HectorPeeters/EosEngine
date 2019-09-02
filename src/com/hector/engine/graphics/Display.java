package com.hector.engine.graphics;

import com.hector.engine.Engine;
import com.hector.engine.EngineStateEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.graphics.events.WindowResizeEvent;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.input.events.MouseButtonEvent;
import com.hector.engine.input.events.MouseMoveEvent;
import com.hector.engine.input.events.MouseScrollEvent;
import com.hector.engine.logging.Logger;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class Display {

    private long window;
    private boolean closing = false;

    private int width, height;

    public boolean create(int width, int height, int samples) {
        this.width = width;
        this.height = height;

        if (!GLFW.glfwInit()) {
            Logger.err("Graphics", "Failed to init GLFW");
            return false;
        }

        GLFW.glfwSetErrorCallback(new GLFWErrorCallback() {
            @Override
            public void invoke(int error, long description) {
                Logger.err("Graphics", "GLFW error [" + error + "]: " + description);
            }
        });

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        if (samples != 0)
            GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, samples);

        if (Engine.DEV_BUILD)
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);

        window = GLFW.glfwCreateWindow(width, height, "Engine", MemoryUtil.NULL, MemoryUtil.NULL);

        if (window == MemoryUtil.NULL) {
            Logger.err("Graphics", "Failed to create GLFW window");
            return false;
        }

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

        setupCallbacks();

        return true;
    }

    private void setupCallbacks() {
        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                GLFW.glfwSetWindowShouldClose(window, true);

            if (action == GLFW.GLFW_REPEAT)
                return;

            EventSystem.publishImmediate(new KeyEvent(key, action == GLFW.GLFW_PRESS));
        });

        GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) -> EventSystem.publishImmediate(new MouseButtonEvent(button, action == GLFW.GLFW_PRESS)));

        GLFW.glfwSetCursorPosCallback(window, (window, xPos, yPos) -> {
            //Convert to normalized coordinates
            EventSystem.publishImmediate(new MouseMoveEvent((xPos / width - 0.5f) * 2f, (yPos / height - 0.5f) * 2f, (int) xPos, (int) yPos));
        });

        GLFW.glfwSetScrollCallback(window, (window, xOffset, yOffset) -> EventSystem.publish(new MouseScrollEvent((float) xOffset, (float) yOffset)));

        GLFW.glfwSetWindowSizeCallback(window, (window, w, h) -> {
            EventSystem.publish(new WindowResizeEvent(w, h));

            GL11.glViewport(0, 0, w, h);
        });
    }

    public void update() {
        if (GLFW.glfwWindowShouldClose(window)) {
            if (!closing)
                EventSystem.publish(new EngineStateEvent(EngineStateEvent.EngineState.STOP));

            closing = true;
            return;
        }

        GLFW.glfwSwapBuffers(window);
    }

    public void pollEvents() {
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
