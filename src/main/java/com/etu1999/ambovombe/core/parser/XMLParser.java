package com.etu1999.ambovombe.core.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * @author rakharrs
 */
public class XMLParser {
    private File file = null;
    private Document document = null;
    private DocumentBuilder documentBuilder = null;

    public XMLParser(String path){
        setFile(path);
    }
    public XMLParser(File file){
        setFile(file);
    }

    public Element getRootElement() throws ParserConfigurationException, IOException, SAXException {
        return getDocument().getDocumentElement();
    }
    public NodeList getElementsByTagName(String tagname) throws ParserConfigurationException, IOException, SAXException {
        return getDocument().getElementsByTagName(tagname);
    }
    public DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if(documentBuilder == null){
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            this.documentBuilder = factory.newDocumentBuilder();
        }
        return documentBuilder;
    }

    public Document getDocument() throws ParserConfigurationException, IOException, SAXException {
        if(document == null)
            setDocument(getDocumentBuilder().parse(getFile()));
        return document;
    }

    protected void setDocument(Document document) {
        this.document = document;
    }

    public void setFile(File file){
        this.file = file;
    }
    public void setFile(String path){
        this.file = new File(path);
    }
    public File getFile() {
        return file;
    }
}
