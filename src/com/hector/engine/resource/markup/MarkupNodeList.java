package com.hector.engine.resource.markup;

import java.util.ArrayList;
import java.util.List;

public class MarkupNodeList {

    //TODO: optimize

    private List<MarkupNode> nodes;

    public MarkupNodeList() {
        nodes = new ArrayList<>();
    }

    public void add(MarkupNode node) {
        nodes.add(node);
    }

    public MarkupNode getNode(String name) {
        for (MarkupNode node : nodes)
            if (node.getName().equals(name))
                return node;

        return null;
    }

    public MarkupNode getNode(int index) {
        return nodes.get(index);
    }

    public List<MarkupNode> getNodes() {
        return nodes;
    }
}
