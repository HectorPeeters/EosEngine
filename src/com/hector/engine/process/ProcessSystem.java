package com.hector.engine.process;

import com.hector.engine.event.Handler;
import com.hector.engine.logging.Logger;
import com.hector.engine.systems.AbstractSystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProcessSystem extends AbstractSystem {

    private List<AbstractProcess> processList = new ArrayList<>();

    public ProcessSystem() {
        super("process", 1000);
    }

    @Override
    protected void init() {

    }

    @Override
    public void update(float delta) {
        updateProcesses(delta);
    }

    @Override
    protected void destroy() {
        clearProcesses();
    }

    @Handler
    private void onAddProcessEvent(AddProcessEvent event) {
        attachProcess(event.process);
    }

    private int updateProcesses(float delta) {
        int successCount = 0;
        int failCount = 0;

        for (int i = 0; i < processList.size(); i++) {
            AbstractProcess process = processList.get(i);

            if (process.getState() == AbstractProcess.State.UNINITIALIZED)
                process.onInit();

            if (process.getState() == AbstractProcess.State.RUNNING)
                process.onUpdate(delta);

            if (process.isDead()) {

                Logger.debug("Process", "Process " + process.getClass().getSimpleName() + " finished with state " + process.getState());

                switch (process.getState()) {
                    case SUCCEEDED: {
                        process.onSuccess();

                        AbstractProcess child = process.removeChild();
                        if (child != null)
                            attachProcess(child);
                        else
                            successCount++;
                        break;
                    }

                    case FAILED: {
                        process.onFail();
                        failCount++;
                        break;
                    }

                    case ABORTED: {
                        process.onAbort();
                        failCount++;
                        break;
                    }
                }

                processList.remove(process);
                i++;
            }
        }

        return ((successCount << 16) | failCount);
    }

    public void attachProcess(AbstractProcess process) {
        processList.add(0, process);
    }

    public void abortProcesses(boolean immediate) {
        Iterator<AbstractProcess> iterator = processList.iterator();
        while (iterator.hasNext()) {
            AbstractProcess process = iterator.next();

            if (process.isAlive()) {
                process.setState(AbstractProcess.State.ABORTED);

                if (immediate) {
                    process.onAbort();
                    iterator.remove();
                }
            }
        }
    }

    public int getProcessCount() {
        return processList.size();
    }

    private void clearProcesses() {
        processList.clear();
    }
}
