package com.hector.engine.process.events;

import com.hector.engine.process.AbstractProcess;

public class AddProcessEvent {

    public final AbstractProcess process;

    public AddProcessEvent(AbstractProcess process) {
        this.process = process;
    }
}
