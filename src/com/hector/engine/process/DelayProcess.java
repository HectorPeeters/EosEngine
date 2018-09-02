package com.hector.engine.process;

public class DelayProcess extends AbstractProcess {

    private long delay;
    private long timeDelayedSoFar;

    public DelayProcess(long delay) {
        this.delay = delay;
        this.timeDelayedSoFar = 0;
    }


    @Override
    protected void onUpdate(float delta) {
        timeDelayedSoFar += delta;

        if (timeDelayedSoFar >= delay)
            succeed();
    }
}
