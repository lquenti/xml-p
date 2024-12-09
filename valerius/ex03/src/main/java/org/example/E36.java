package org.example;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class E36 {
    public static void run() throws Exception {
        // Create SAX parser with namespace awareness
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);  // Enable namespace awareness
        SAXParser saxParser = factory.newSAXParser();

        // Set up the output handler
        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler handler = tf.newTransformerHandler();
        
        // Configure output formatting
        Transformer transformer = handler.getTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        
        // First, write the XML declaration and DTD manually
        FileWriter writer = new FileWriter("output_3_6.xml");
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<!DOCTYPE mondial SYSTEM \"mondial.dtd\">\n");
        writer.close();
        
        // Append the transformed content
        FileWriter contentWriter = new FileWriter("output_3_6.xml", true);
        handler.setResult(new StreamResult(contentWriter));

        // Create custom ContentHandler that modifies country names
        XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(new CountryIndexHandler(handler));

        // Parse and transform
        reader.parse(new InputSource("mondial.xml"));
        contentWriter.close();
    }
}

// Custom handler that adds indices to country names
class CountryIndexHandler extends DefaultHandler {
    private final TransformerHandler handler;
    private int countryIndex = 0;
    private boolean insideCountry = false;
    private boolean insideName = false;
    private StringBuilder currentName = new StringBuilder();

    public CountryIndexHandler(TransformerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) 
            throws SAXException {
        if ("country".equals(qName)) {
            insideCountry = true;
            countryIndex++;
        } else if ("name".equals(qName) && insideCountry) {
            insideName = true;
            currentName.setLength(0);
        }
        
        // Pass through all attributes
        handler.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("country".equals(qName)) {
            insideCountry = false;
        } else if ("name".equals(qName) && insideCountry) {
            // Modify the name by adding the index
            String modifiedName = currentName.toString().trim() + " (" + (countryIndex - 1) + ")";
            char[] chars = modifiedName.toCharArray();
            handler.characters(chars, 0, chars.length);
            insideName = false;
        }
        
        handler.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (insideName && insideCountry) {
            currentName.append(ch, start, length);
        } else {
            handler.characters(ch, start, length);
        }
    }
}
