package com.hector.engine.graphics.ui;

import com.hector.engine.entity.AbstractEntityComponent;
import com.hector.engine.entity.events.AddEntityComponentEvent;
import com.hector.engine.entity.events.RemoveEntityComponentEvent;
import com.hector.engine.event.EventSystem;
import com.hector.engine.event.Handler;
import com.hector.engine.graphics.components.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class FontRenderer {

    public Font currentFont;

    private List<TextComponent> textComponents = new ArrayList<>();

    public FontRenderer(Font font) {
        this.currentFont = font;

        EventSystem.subscribe(this);
    }

    @Handler
    private void onTextComponentAdded(AddEntityComponentEvent components) {
        for (AbstractEntityComponent entityComponent : components.components) {
            if (entityComponent instanceof TextComponent) {
                textComponents.add((TextComponent) entityComponent);
            }
        }
    }

    @Handler
    private void onTextComponentRemoved(RemoveEntityComponentEvent components) {
        for (AbstractEntityComponent entityComponent : components.components) {
            if (entityComponent instanceof TextComponent) {
                textComponents.remove(entityComponent);
            }
        }
    }

    public void draw() {
        for (TextComponent textComponent : textComponents) {

        }
    }

}
