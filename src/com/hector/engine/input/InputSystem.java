package com.hector.engine.input;

import com.hector.engine.event.Handler;
import com.hector.engine.input.events.KeyEvent;
import com.hector.engine.input.events.MouseButtonEvent;
import com.hector.engine.input.events.MouseMoveEvent;
import com.hector.engine.systems.AbstractSystem;
import org.joml.Vector2f;

public class InputSystem extends AbstractSystem {

    private static final int KEY_COUNT = 1024;
    private static final int BUTTON_COUNT = 16;

    private static boolean[] keysDown;
    private static boolean[] buttonsDown;

    private static float mouseX;
    private static float mouseY;

//    private List<Controller> controllers;

    public InputSystem() {
        super("input", 900);
    }

    @Override
    protected void init() {
        keysDown = new boolean[KEY_COUNT];
        buttonsDown = new boolean[BUTTON_COUNT];

//        controllers = new ArrayList<>();

//        Controller[] contrs = ControllerEnvironment.getDefaultEnvironment().getControllers();

//        controllers.addAll(Arrays.asList(contrs));
    }

    @Override
    public void preUpdate(float delta) {
//        for (Controller controller : controllers) {
//            controller.poll();
//
//            if (controller.getType() == Controller.Type.GAMEPAD)
//                System.out.println(controller.getComponent(Component.Identifier.Axis.X).getPollData());
//        }
    }

    @Handler
    private void onKeyEvent(KeyEvent event) {
        keysDown[event.keycode] = event.pressed;
    }

    @Handler
    private void onMouseMoveEvent(MouseMoveEvent event) {
        mouseX = (float) event.xpos;
        mouseY = (float) event.ypos;
    }

    @Handler
    private void onMouseButtonEvent(MouseButtonEvent event) {
        buttonsDown[event.button] = event.pressed;
    }

    @Override
    protected void destroy() {

    }

    public static boolean isKeyDown(int keyCode) {
        return keysDown[keyCode];
    }

    public static boolean isButtonDown(int button) {
        return buttonsDown[button];
    }

    public static float getMouseX() {
        return mouseX;
    }

    public static float getMouseY() {
        return mouseY;
    }

    public static Vector2f getMousePosition() {
        return new Vector2f(mouseX, mouseY);
    }
}
