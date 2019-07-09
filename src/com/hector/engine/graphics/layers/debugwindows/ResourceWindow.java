package com.hector.engine.graphics.layers.debugwindows;

import com.hector.engine.graphics.layers.AbstractDebugWindow;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.AbstractResource;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ResourceWindow extends AbstractDebugWindow {

    @Override
    public void create() {

    }

    @Override
    public void draw(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);

            AbstractResource[] resources = ResourceManager.getResources().toArray(new AbstractResource[0]);

            nk_layout_row_static(ctx, 25, 100, resources.length);
            if (nk_begin(ctx, "Resources", nk_rect(x, y, 400, resources.length * 40 + 50, rect), NK_WINDOW_SCALABLE | NK_WINDOW_MOVABLE | NK_WINDOW_MINIMIZABLE | NK_WINDOW_TITLE)) {

                for (int i = 0; i < resources.length; i++) {
                    String name = "[" + resources[i].getClass().getSimpleName().replace("Resource", "") + "]\t" + resources[i].getPath();

                    nk_layout_row_dynamic(ctx, 25, 1);
                    nk_label(ctx, name, NK_TEXT_ALIGN_LEFT);
                }
            }

            nk_end(ctx);
        }

    }


    @Override
    public void destroy() {

    }
}
