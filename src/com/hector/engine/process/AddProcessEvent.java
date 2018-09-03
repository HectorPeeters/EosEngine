package com.hector.engine.process;

public class AddProcessEvent {

    public final AbstractProcess process;

    public AddProcessEvent(AbstractProcess process) {
        this.process = process;
    }
}
