package com.hector.engine.task;

import com.hector.engine.logging.Logger;

public abstract class AbstractProcess {

    private State state;
    private AbstractProcess child;

    public AbstractProcess() {
        state = State.UNINITIALIZED;
    }

    protected void onInit() {
        state = State.RUNNING;
    }

    protected abstract void onUpdate(float delta);

    protected void onSuccess() {}

    protected void onFail() {}

    protected void onAbort() {}


    public void succeed() {
        state = State.SUCCEEDED;
    }

    public void fail() {
        state = State.FAILED;
    }

    public void pause() {
        if (state == State.RUNNING)
            state = State.PAUSED;
        else
            Logger.warn("Process", "Attempt to pause process that isn't running");
    }

    public void unpause() {
        if (state == State.PAUSED)
            state = State.RUNNING;
        else
            Logger.warn("Process", "Attempt to unpause process that isn't paused");
    }


    public void attachChild(AbstractProcess child) {
        if (this.child != null)
            this.child.attachChild(child);
        else
            this.child = child;
    }

    public AbstractProcess removeChild() {
        AbstractProcess process = this.child;
        this.child = null;
        return process;
    }

    public AbstractProcess getChild() {
        return child;
    }


    protected void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public boolean isAlive() {
        return (state == State.RUNNING || state == State.PAUSED);
    }

    public boolean isDead() {
        return (state == State.SUCCEEDED || state == State.FAILED || state == State.ABORTED);
    }

    public boolean isRemoved() {
        return state == State.REMOVED;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    public enum State {
        UNINITIALIZED,
        REMOVED,
        RUNNING,
        PAUSED,
        SUCCEEDED,
        FAILED,
        ABORTED,
    }

}
