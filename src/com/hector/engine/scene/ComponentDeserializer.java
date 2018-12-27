package com.hector.engine.scene;

import com.google.gson.*;
import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.graphics.components.AnimationComponent;
import com.hector.engine.logging.Logger;
import com.hector.engine.physics.components.RigidBodyComponent;
import com.hector.engine.scripting.components.GroovyScriptComponent;

import java.lang.reflect.Type;

public class ComponentDeserializer implements JsonDeserializer<AbstractEntityComponent> {

    //TODO: implement custom deserializer for every component so each constructor can be called

    @Override
    public AbstractEntityComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) jsonElement;
        JsonElement typeName = jsonObject.get("type");

        //TODO: Invent better alternative because this is sad...
        switch (typeName.getAsString()) {
            case "Animation":
                return context.deserialize(jsonElement, AnimationComponent.class);
            case "Rigidbody":
                return context.deserialize(jsonElement, RigidBodyComponent.class);
            case "Script":
                return context.deserialize(jsonElement, GroovyScriptComponent.class);
        }

        Logger.err("Scene", "Unrecognized object in scene " + typeName);

        return null;
    }
}
