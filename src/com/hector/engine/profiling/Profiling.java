package com.hector.engine.profiling;

import com.hector.engine.logging.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Profiling {

    private static DecimalFormat decimalFormat = new DecimalFormat("#########.######");

    private static Map<String, ProfilingInstance> profilingMap = new HashMap<>();

    public static void start(String name) {
        if (!profilingMap.containsKey(name))
            profilingMap.put(name, new ProfilingInstance(name));

        profilingMap.get(name).start();
    }

    public static void stop(String name) {
        if (!profilingMap.containsKey(name)) {
            Logger.warn("Profiling", "No ProfilingInstance with name " + name + " to stop");
            return;
        }

        profilingMap.get(name).stop();
    }

    public static void printProfilingInfo() {
        List<ProfilingInstance> sortedValues = new ArrayList<>(profilingMap.values());
        sortedValues.sort((o1, o2) -> {
            if (o1.timePerIterationMS() == o2.timePerIterationMS())
                return 0;
            return o1.timePerIterationMS() > o2.timePerIterationMS() ? -1 : 1;
        });

        Logger.debug("Profiling", "Profiling info: ");
        for (ProfilingInstance instance : sortedValues)
            Logger.debug("Profiling", instance.getName() + " -> iterations: " + instance.getNumIterations() + ", total time: " + instance.getTotalTimeMS() + ", avg time: " + decimalFormat.format(instance.timePerIterationMS()));
    }

}
