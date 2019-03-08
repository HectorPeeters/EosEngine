package com.hector.engine.xml;

import com.hector.engine.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

public class XMLConfigFile {

    private Map<String, String> entries = new HashMap<>();

    private XMLLoader loader;

    public XMLConfigFile(String file) {
        loader = new XMLLoader(file);
    }

    public boolean load() {
        loader.load();

        Element root = loader.getDocumentElement();
        if (!root.getNodeName().equals("Config")) {
            Logger.warn("Engine", "Error");
            return false;
        }

        NodeList list = root.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);

            if (!n.getNodeName().equals("Entry"))
                continue;

            Element entryElement = (Element) n;

            String name = entryElement.getAttribute("name");
            String value = entryElement.getAttribute("value");
            entries.put(name, value);
        }

        Logger.debug("Engine", "Loaded \"" + loader.getPath() + "\"config file with " + entries.size() + " values");

        return true;
    }

    public String getString(String name) {
        return entries.get(name);
    }

    public int getInt(String name) {
        return Integer.parseInt(entries.get(name));
    }

    public float getFloat(String name) {
        return Float.parseFloat(entries.get(name));
    }

    public boolean getBoolean(String name) {
        return Boolean.parseBoolean(entries.get(name));
    }

}
