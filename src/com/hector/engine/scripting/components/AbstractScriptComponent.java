package com.hector.engine.scripting.components;

import com.hector.engine.entity.AbstractEntityComponent;

public abstract class AbstractScriptComponent extends AbstractEntityComponent {

    public abstract void updateScript(float delta);

}
