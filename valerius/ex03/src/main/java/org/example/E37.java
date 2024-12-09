package org.example;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;

public class E37 {
    public static void run() throws Exception {
        // Create input factory
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        // Create output factory
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        
        // Create reader and writer
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileInputStream("mondial.xml"));
        
        // First write XML declaration and DTD manually
        FileWriter writer = new FileWriter("output_3_7.xml");
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<!DOCTYPE mondial SYSTEM \"mondial.dtd\">\n");
        writer.close();
        
        // Now append the transformed content
        writer = new FileWriter("output_3_7.xml", true);
        XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(writer);
        
        // Track state
        boolean insideCountry = false;
        boolean insideName = false;
        int countryIndex = 0;
        StringBuilder currentName = new StringBuilder();
        
        // Process events
        while (reader.hasNext()) {
            int event = reader.next();
            
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    
                    if ("country".equals(elementName)) {
                        insideCountry = true;
                        countryIndex++;
                    } else if ("name".equals(elementName) && insideCountry) {
                        insideName = true;
                        currentName.setLength(0);
                    }
                    
                    // Write start element
                    xmlWriter.writeStartElement(elementName);
                    
                    // Copy attributes
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        xmlWriter.writeAttribute(
                            reader.getAttributeLocalName(i),
                            reader.getAttributeValue(i)
                        );
                    }
                    break;
                    
                case XMLStreamConstants.CHARACTERS:
                    String text = reader.getText().trim();
                    if (text.isEmpty()) {
                        break;
                    }
                    
                    if (insideName && insideCountry) {
                        currentName.append(text);
                    } else {
                        xmlWriter.writeCharacters(text);
                    }
                    break;
                    
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    
                    if ("country".equals(elementName)) {
                        insideCountry = false;
                    } else if ("name".equals(elementName) && insideCountry) {
                        // Write modified country name
                        String modifiedName = currentName.toString().trim() + 
                                           " (" + (countryIndex - 1) + ")";
                        xmlWriter.writeCharacters(modifiedName);
                        insideName = false;
                    }
                    
                    xmlWriter.writeEndElement();
                    break;
            }
        }
        
        // Clean up
        xmlWriter.flush();
        xmlWriter.close();
        reader.close();
    }
} 