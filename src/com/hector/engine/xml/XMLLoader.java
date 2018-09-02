package com.hector.engine.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLLoader {

    private String path;

    private Document document;

    public XMLLoader(String path) {
        this.path = path;
    }

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
            document = builder.parse(new File(path));
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
