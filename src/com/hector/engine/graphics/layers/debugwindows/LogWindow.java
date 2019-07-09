package com.hector.engine.graphics.layers.debugwindows;

import com.hector.engine.event.EventSystem;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.layers.AbstractDebugWindow;
import com.hector.engine.logging.events.LogEvent;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class LogWindow extends AbstractDebugWindow {

    private List<String> log;

    @Override
    public void create() {
        log = new ArrayList<>();

        EventSystem.subscribe(this);
    }

    @Handler
    private void onLogEvent(LogEvent event) {
        log.add(event.message);
    }

    @Override
    public void draw(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            if (nk_begin(ctx, "Log", nk_rect(x, y, 230, 250, rect), NK_WINDOW_BORDER | NK_WINDOW_MOVABLE | NK_WINDOW_SCALABLE | NK_WINDOW_MINIMIZABLE | NK_WINDOW_TITLE)) {
                nk_layout_row_static(ctx, 100, 300, 1);

                String log = getLogString();
//                nk_label_wrap(ctx, log);
                nk_text(ctx, log, NK_TEXT_ALIGN_LEFT);
            }

            nk_end(ctx);
        }
    }

    private String getLogString() {
        String result = "";
        for(String s : log)
            result += s + "\n";

        return result;
    }

    @Override
    public void destroy() {
        EventSystem.unsubscribe(this);
    }
}
