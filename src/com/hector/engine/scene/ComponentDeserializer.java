package com.hector.engine.scene;

import com.google.gson.*;
import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.graphics.components.AnimationComponent;
import com.hector.engine.graphics.components.TextureComponent;
import com.hector.engine.logging.Logger;
import com.hector.engine.physics.components.RigidbodyComponent;
import com.hector.engine.scripting.components.GroovyScript;
import com.hector.engine.scripting.components.GroovyScriptComponent;
import org.reflections.Reflections;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ComponentDeserializer implements JsonDeserializer<AbstractEntityComponent> {

    private Map<String, Class<? extends AbstractEntityComponent>> componentClasses;

    public ComponentDeserializer() {
        componentClasses = new HashMap<>();

        //TODO: use reflections but fast
        //TODO: optimize this in some way because it takes more than 2 seconds
//        Set<Class<? extends AbstractEntityComponent>> componentClassList = new Reflections().getSubTypesOf(AbstractEntityComponent.class);
//
//        for (Class<? extends AbstractEntityComponent> component : componentClassList) {
//            componentClasses.put(component.getSimpleName(), component);
//        }

        componentClasses.put(AnimationComponent.class.getSimpleName(), AnimationComponent.class);
        componentClasses.put(TextureComponent.class.getSimpleName(), TextureComponent.class);
        componentClasses.put(RigidbodyComponent.class.getSimpleName(), RigidbodyComponent.class);
        componentClasses.put(GroovyScriptComponent.class.getSimpleName(), GroovyScriptComponent.class);

        Logger.debug("Scene", "Registered " + componentClasses.size() + " components");
    }

    @Override
    public AbstractEntityComponent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = (JsonObject) jsonElement;
        JsonElement typeName = jsonObject.get("type");

        Class<? extends AbstractEntityComponent> componentClass = componentClasses.get(typeName.getAsString() + "Component");

        if (componentClass == null) {
            Logger.err("Scene", "Unrecognized object in scene " + typeName.getAsString() + "Component");
            return null;
        }

        return context.deserialize(jsonElement, componentClass);
    }
}
