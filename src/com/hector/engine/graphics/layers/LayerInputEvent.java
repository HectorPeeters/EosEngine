package com.hector.engine.graphics.layers;

public abstract class LayerInputEvent {

    public enum EventType {
        KEY_PRESSED,
        KEY_RELEASED,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_MOVED
    }

    public final EventType type;
    private boolean consumed = false;

    public LayerInputEvent(EventType type) {
        this.type = type;
    }

    public void consume() {
        this.consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }


    public class LayerKeyPressedEvent extends LayerInputEvent {

        public final int keycode;

        public LayerKeyPressedEvent(int keycode) {
            super(EventType.KEY_PRESSED);

            this.keycode = keycode;
        }
    }

    public class LayerKeyReleasedEvent extends LayerInputEvent {

        public final int keycode;

        public LayerKeyReleasedEvent(int keycode) {
            super(EventType.KEY_RELEASED);

            this.keycode = keycode;
        }
    }

    public class LayerMousePressedEvent extends LayerInputEvent {

        public final int button;
        public final float x, y;

        public LayerMousePressedEvent(int button, float x, float y) {
            super(EventType.MOUSE_PRESSED);

            this.button = button;
            this.x = x;
            this.y = y;
        }
    }

    public class LayerMouseReleasedEvent extends LayerInputEvent {

        public final int button;
        public final float x, y;

        public LayerMouseReleasedEvent(int button, float x, float y) {
            super(EventType.MOUSE_RELEASED);

            this.button = button;
            this.x = x;
            this.y = y;
        }
    }

    public class LayerMouseMovedEvent extends LayerInputEvent {

        public final float x, y;

        public LayerMouseMovedEvent(float x, float y) {
            super(EventType.MOUSE_MOVED);

            this.x = x;
            this.y = y;
        }
    }

}
