package com.etu1999.ambovombe.core.parser;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NodeList;

import com.etu1999.ambovombe.utils.Misc;

/**
 * A utility class for reading and parsing XML files.
 * @author mirindra
 */
public class XMLReader {
    
    /**
     * Reads an XML file and returns the root element of the document.
     *
     * @param filePath The path to the XML file to be read.
     * @return The root element of the XML document.
     * @throws Exception If the file does not exist or if there is an issue parsing the XML.
     */
    public static Element readXml(String filePath) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        filePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8.toString());
        File xmlFile = new File(Misc.currentLocation() + filePath);
        if (!xmlFile.exists())
            throw new Exception("database.xml file is missing in your project root directory - : " + Misc.currentLocation());
        Document document = builder.parse(xmlFile);
        Element rootElement = document.getDocumentElement();
        
        return rootElement;
    }

    /**
     * Retrieves the text content of the first element with the given tag name within the provided XML element.
     *
     * @param element The XML element to search within.
     * @param tagName The tag name of the element to retrieve.
     * @return The text content of the first matching element or null if no match is found.
     */
    public static String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Retrieves the first child element with the specified tag name and 'dbname' attribute matching the given value.
     *
     * @param element The XML element to search within.
     * @param tagName The tag name of the child elements to search for.
     * @param dbname  The value to match in the 'dbname' attribute.
     * @return The first matching child element or null if no match is found.
     */
    public static Element getElement(Element element, String tagName, String dbname) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                if (XMLReader.getElementValue(el, "connection_id").equals(dbname)) {
                    return el;
                }
            }
        }
        return null;
    }

    public static Element getElements(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        nodeList.getLength();
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element el = (Element) nodeList.item(i);
                /*if (XMLReader.getElementValue(el, "connection_id").equals(dbname)) {
                    return el;
                }*/
            }
        }
        return null;
    }
}
