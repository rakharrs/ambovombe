package com.etu1999.ambovombe.core.connection.config;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.etu1999.ambovombe.core.connection.GConnection;
import com.etu1999.ambovombe.core.parser.XMLReader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The configuration class for connection
 * @author rakharrs
 */
@Getter @Setter @NoArgsConstructor
public class GConfiguration {

    private HashMap<String, GConnection> connections = new HashMap<>();
    

    /** the (database.xml) xml file path */
    private String xml;

    public GConfiguration(String xml){
        setXml(xml);
        try {
            initConnections();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Retrieving the connection(s) attribute(s) from the xml
     * And mapping it to the (Hashmap) connections attribute of this class
     * @return the HashMap connections
     * @throws Exception
     */
    public HashMap<String, GConnection> initConnections() throws Exception{
        Element e = XMLReader.readXml(getXml());
        NodeList nl = e.getElementsByTagName("connection");
        if(nl != null && nl.getLength() > 0)
            for (int i = 0; i < nl.getLength(); i++)
                mappingElementConnection((Element) nl.item(i), false);
        return getConnections();
    }

    /**
     * Mapping the connection element to the (HashMap) connections attribute
     * from its name as a key and "it" overall as a value
     * If the defining value is set to true then the connection will
     * directly be created and defined in the GConnection object
     * @param connectioElement the connection element from the xml
     * @param defining the defining value
     * @throws Exception
     */
    public void mappingElementConnection(Element connectioElement, boolean defining) throws Exception{
        GConnection gc = new GConnection(connectioElement);
        if(defining) gc.defineConnection();
        getConnections().put(gc.getName(), gc);
    }
}
