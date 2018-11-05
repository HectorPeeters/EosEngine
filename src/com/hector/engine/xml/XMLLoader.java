package com.hector.engine.xml;

import com.hector.engine.resource.ResourceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * This is a very basic XMLLoader class. Basically just a wrapper for the w3c dom xml loading code. It makes it easier
 * to load an XML document but adds no special functionality.
 */
public class XMLLoader {

    private String path;

    private Document document;

    /**
     * Just a basic constructor which takes in a path and sets the class variable.
     * @param path
     */
    public XMLLoader(String path) {
        this.path = path;
    }

    /**
     * This method loads the XML file and sets the document variable.
     * @return Returns true if the document was loaded correctly, false if not
     */
    public boolean load() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.err.println("Failed to create new documentBuilder");
            return false;
        }

        try {
            document = builder.parse(ResourceManager.getStreamResource(path).getStream());
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            System.err.println("Failed to parse xml file: " + path);
            return false;
        }

        return true;
    }

    public Document getDocument() {
        return document;
    }

    public Element getDocumentElement() {
        return document.getDocumentElement();
    }

    public String getPath() {
        return path;
    }
}
