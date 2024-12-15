package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class E36a {
    public static void run() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();

        FileWriter writer = new FileWriter("output_3_6a.xml");

        // create custom handler that writes directly to the file
        XMLReader reader = saxParser.getXMLReader();
        reader.setContentHandler(new AltHandler(writer));

        // parse and transform
        reader.parse(new InputSource("mondial.xml"));
        writer.close();

        // Add CAT to mondial.xml
        // "Subtract" CAT from E
        // TODO: Population Growth
        // TODO: infant mortality
        // TODO: GDP
        // TODO: Inflation EUR?
        // TODO: Unemployment
        // TODO: Ethnic Groups
        // TODO: Religions
        // TODO: Borders
    }
}

class AltHandler extends DefaultHandler {
    private final Writer writer;
    private int countryIndex = 0;
    private boolean insideCountry = false;
    private boolean insideName = false;
    private StringBuilder currentName = new StringBuilder();
    private int depth = 0;
    private boolean firstElement = true;
    private boolean skipProvince = false;

    public AltHandler(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void startDocument() throws SAXException {
        try {
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
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            writer.write("</mondial>");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        try {
            if ("province".equals(qName) && "prov-Spain-11".equals(attributes.getValue("id"))) {
                skipProvince = true;
                return; // Skip processing this element
            }
            if (skipProvince) {
                return; // Skip processing if inside the province to be deleted
            }
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
            if ("province".equals(qName) && skipProvince) {
                skipProvince = false;
                return; // Skip closing tag for the province
            }
            if (skipProvince) {
                return; // Skip processing if inside the province to be deleted
            }
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
