package org.example;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;
import org.jdom2.DocType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class E34 {
    private static Element ROOT;

    public static void run() throws IOException, JDOMException {
        // Read mondial.xml into a JDOM object,
        E34.ROOT = E31.readMondial();

        // Update it using the JDOM operations
        // 1. find all countries
        List<Element> countries = E34.ROOT.getChildren("country");
        for (int i = 0; i < countries.size(); i++) {
            Element country = countries.get(i);
            Element name = country.getChild("name");
            if (name == null) {
                continue;
            }
            // 2. Update the name of the country to include the index of the country in the list
            name.setText(name.getText() + " (" + i + ")");
        }
        // 4. Output the updated XML to a file with DTD reference
        FileWriter writer = new FileWriter("output_3_4.xml");
        // Write XML declaration and DOCTYPE manually
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<!DOCTYPE mondial SYSTEM \"mondial.dtd\">\n");
        
        // Output the XML content
        XMLOutputter xmlOutput = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setOmitDeclaration(true);  // Skip XML declaration since we wrote it manually
        xmlOutput.setFormat(format);
        xmlOutput.output(E34.ROOT, writer);
        writer.close();
    }
}
