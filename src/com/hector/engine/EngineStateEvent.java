package com.hector.engine;

public class EngineStateEvent {

    public final EngineState state;

    public EngineStateEvent(EngineState state) {
        this.state = state;
    }

    public enum EngineState {
        STOP,
        PAUSE,
        UNPAUSE
    }

}
