package com.hector.engine.graphics.layers.debugwindows;

import com.hector.engine.graphics.layers.AbstractDebugWindow;
import com.hector.engine.utils.FloatRingBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ProfileWindow extends AbstractDebugWindow {

    private FloatRingBuffer ringBuffer;

    @Override
    public void create() {
        ringBuffer = new FloatRingBuffer(400);
    }

    @Override
    public void draw(NkContext ctx, int x, int y) {
        ringBuffer.add(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            if (nk_begin(ctx, "Profiling", nk_rect(x, y, 400, 300, rect), NK_WINDOW_SCALABLE | NK_WINDOW_MOVABLE | NK_WINDOW_MINIMIZABLE | NK_WINDOW_TITLE)) {
//                nk_plot(ctx, NK_CHART_LINES, ringBuffer.getAsArray(), ringBuffer.getAsArray().length, 0);
                nk_layout_row_dynamic(ctx, 250, 1);
                if (nk_chart_begin(ctx, NK_CHART_LINES, ringBuffer.getSize(), 0, 1024 * 1024 * 256)) { //0.25 GB
                    for (int i = 0; i < ringBuffer.getSize(); i++) {
                        nk_chart_push(ctx, ringBuffer.getAsArray()[i]);
                    }

                    nk_chart_end(ctx);
                }
            }

            nk_end(ctx);
        }
    }

    @Override
    public void destroy() {

    }
}
