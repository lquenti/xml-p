package me.valerius;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
public class JdomHtml {
    public static void parseAndTransform(String inputFile, String outputFile) {
        try {
            SAXBuilder builder = new SAXBuilder();
            builder.setIgnoringBoundaryWhitespace(true);

            Document document = builder.build(new File(inputFile));
            Element rootElement = document.getRootElement();

            removeElements(rootElement, "script");
            removeElements(rootElement, "style");

            // Output the modified document
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            try (FileWriter writer = new FileWriter(outputFile)) {
                outputter.output(document, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removeElements(Element parent, String elementName) {
        // Create a list to store elements to be removed
        List<Element> elements = parent.getChildren(elementName);

        // Remove all matching elements
        elements.forEach(Element::detach);

        // Recursively process remaining child elements
        for (Element child : parent.getChildren()) {
            removeElements(child, elementName);
        }
    }

    public static void main(String[] args) {
        parseAndTransform("montblanc.html", "montblanc_jdom.html");
    }
}