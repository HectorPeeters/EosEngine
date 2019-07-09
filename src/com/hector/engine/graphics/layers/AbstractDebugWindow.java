package com.hector.engine.graphics.layers;

import org.lwjgl.nuklear.NkContext;

public abstract class AbstractDebugWindow {

    public abstract void create();

    public abstract void draw(NkContext context, int x, int y);

    public abstract void destroy();
}
