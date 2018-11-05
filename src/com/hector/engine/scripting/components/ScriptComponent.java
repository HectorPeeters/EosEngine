package com.hector.engine.scripting.components;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.logging.Logger;
import com.hector.engine.resource.ResourceManager;
import com.hector.engine.resource.resources.TextResource;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class ScriptComponent extends AbstractEntityComponent {

    private Globals globals;

    private String path;

    public ScriptComponent(String path) {
        this.path = path;
    }

    @Override
    public void init() {
        TextResource source = ResourceManager.getResource(path);

        globals = JsePlatform.standardGlobals();

        globals.set("parent", CoerceJavaToLua.coerce(parent));

        globals.set("print", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue luaValue) {
                Logger.info("Lua", luaValue.toString());
                return LuaValue.NIL;
            }
        });

        globals.load(source.getResource()).call();

        LuaValue init = globals.get("init");
        if (init != LuaValue.NIL)
            init.call();
    }

    public void updateScript(float delta) {
        LuaValue update = globals.get("update");

        if (update != LuaValue.NIL)
            update.call(LuaValue.valueOf(delta));
    }
}
