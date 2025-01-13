package org.example;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;

public class E37 {
    public static void run() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        
        // Create reader and writer
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileInputStream("mondial.xml"));
        
        // write xml declaration
        FileWriter writer = new FileWriter("output_3_7.xml");
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<!DOCTYPE mondial SYSTEM \"mondial.dtd\">\n");
        writer.close();
        
        // append the transformed content
        writer = new FileWriter("output_3_7.xml", true);
        XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(writer);
        
        // Track state (index and name)
        boolean insideCountry = false;
        boolean insideName = false;
        int countryIndex = 0;
        StringBuilder currentName = new StringBuilder();
        
        // process events
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
                    
                    // write start element
                    xmlWriter.writeStartElement(elementName);
                    
                    // copy attributes
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
                        // write modified country name
                        String modifiedName = currentName.toString().trim() + 
                                           " (" + (countryIndex - 1) + ")";
                        xmlWriter.writeCharacters(modifiedName);
                        insideName = false;
                    }
                    
                    xmlWriter.writeEndElement();
                    break;
            }
        }
        
        // clean up
        xmlWriter.flush();
        xmlWriter.close();
        reader.close();
    }
} 