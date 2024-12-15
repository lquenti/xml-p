package org.example;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.*;
import java.io.*;

public class E36 {
    public static void run() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();

        FileWriter writer = new FileWriter("output_3_6.xml");
        
        // create custom handler that writes directly to the file
        XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(new AltHandler(writer));

        // parse and transform
        reader.parse(new InputSource("mondial.xml"));
        writer.close();
    }
}

class CountryIndexHandler extends DefaultHandler {
    private final Writer writer;
    private int countryIndex = 0;
    private boolean insideCountry = false;
    private boolean insideName = false;
    private StringBuilder currentName = new StringBuilder();
    private int depth = 0;
    private boolean firstElement = true;

    public CountryIndexHandler(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!DOCTYPE mondial SYSTEM \"mondial.dtd\">\n");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) 
            throws SAXException {
        try {
            depth++;
            // Indent
            if (!firstElement) {
                writer.write("\n");
                writeIndent();
            }
            firstElement = false;

            writer.write("<" + qName);
            
            // Write attributes
            for (int i = 0; i < attributes.getLength(); i++) {
                writer.write(" " + attributes.getQName(i) + "=\"" + 
                           escapeXml(attributes.getValue(i)) + "\"");
            }
            writer.write(">");

            if ("country".equals(qName)) {
                insideCountry = true;
                countryIndex++;
            } else if ("name".equals(qName) && insideCountry) {
                insideName = true;
                currentName.setLength(0);
            }
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            depth--;
            if ("country".equals(qName)) {
                insideCountry = false;
            } else if ("name".equals(qName) && insideCountry) {
                insideName = false;
            }
            writer.write("</" + qName + ">");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            if (insideName && insideCountry) {
                currentName.append(ch, start, length);
                String name = currentName.toString().trim();
                writer.write(escapeXml(name + " (" + (countryIndex - 1) + ")"));
            } else {
                writer.write(escapeXml(new String(ch, start, length)));
            }
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    private void writeIndent() throws IOException {
        for (int i = 0; i < depth; i++) {
            writer.write("  ");
        }
    }

    private String escapeXml(String input) {
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
}
