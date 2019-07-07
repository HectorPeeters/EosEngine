package com.hector.engine.graphics.layers;

import org.lwjgl.nuklear.NkContext;

public abstract class DebugWindow {

    public abstract void draw(NkContext context, int x, int y);

}
