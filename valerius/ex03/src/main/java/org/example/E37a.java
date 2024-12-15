package org.example;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;

public class E37a {
    public static void run() throws Exception {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        
        // Create reader and writer
        XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileInputStream("mondial.xml"));
        
        // write xml declaration and initial data
        FileWriter writer = new FileWriter("output_3_7.xml");
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<!DOCTYPE mondial SYSTEM \"mondial.dtd\">\n");
        writer.write("<mondial>\n");
        writer.write("<country car_code=\"CAT\">\n" +
                "  <name>Catalonia</name>\n" +
                "  <population_growth>0.8</population_growth>\n" +
                "  <infant_mortality>2.5</infant_mortality>\n" +
                "  <gdp_total>204189</gdp_total>\n" +
                "  <gdp_agri>3</gdp_agri>\n" +
                "  <gdp_ind>37</gdp_ind>\n" +
                "  <gdp_serv>60</gdp_serv>\n" +
                "  <inflation>1.5</inflation>\n" +
                "  <unemployment>12.6</unemployment>\n" +
                "  <indep_date from=\"E\">2018-04-01</indep_date>\n" +
                "  <ethnicgroup percentage=\"100\">Mediterranean Nordic</ethnicgroup>\n" +
                "  <religion percentage=\"52.4\">Roman Catholic</religion>\n" +
                "  <religion percentage=\"2.5\">Protestant</religion>\n" +
                "  <religion percentage=\"7.3\">Muslim</religion>\n" +
                "  <religion percentage=\"1.3\">Buddhist</religion>\n" +
                "  <religion percentage=\"1.2\">Christian Orthodox</religion>\n" +
                "  <language percentage=\"52\">Spanish</language>\n" +
                "  <language percentage=\"41.5\">Catalan</language>\n" +
                "  <language percentage=\"0.1\">Occitan</language>\n" +
                "  <border country=\"AND\" length=\"65\"/>\n" +
                "  <border country=\"F\" length=\"300\"/>\n" +
                "  <border country=\"E\" length=\"320\"/>\n" +
                "</country>\n");
        writer.close();
        
        // append the transformed content
        writer = new FileWriter("output_3_7.xml", true);
        XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(writer);
        
        // Track state
        boolean insideCountry = false;
        boolean insideName = false;
        boolean skipProvince = false;
        int countryIndex = 0;
        StringBuilder currentName = new StringBuilder();
        
        // process events
        while (reader.hasNext()) {
            int event = reader.next();
            
            switch (event) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    
                    // Check if this is the province to skip
                    if ("province".equals(elementName) && 
                        "prov-Spain-11".equals(reader.getAttributeValue(null, "id"))) {
                        skipProvince = true;
                        break;
                    }
                    
                    // Skip if inside the province to be deleted
                    if (skipProvince) {
                        break;
                    }
                    
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
                    if (skipProvince) {
                        break;
                    }
                    
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
                    
                    if ("province".equals(elementName) && skipProvince) {
                        skipProvince = false;
                        break;
                    }
                    
                    if (skipProvince) {
                        break;
                    }
                    
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
        
        // Append closing mondial tag
        writer = new FileWriter("output_3_7.xml", true);
        writer.write("</mondial>");
        writer.close();
    }
} 