package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class E36a {
    public static void main(String[] args) throws Exception {
        E36a ex = new E36a();
        ex.run();
    }

    public void run() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();

        FileWriter writer = new FileWriter("output_3_6a.xml");
        XMLReader reader = saxParser.getXMLReader();

        reader.setContentHandler(new InitHandler(writer));
        reader.parse(new InputSource("mondial.xml"));
        reader.setContentHandler(new CataloniaHandler(writer));
        reader.parse(new InputSource("mondial.xml"));
        reader.setContentHandler(new SwitchProvinceHandler(writer));
        reader.parse(new InputSource("mondial.xml"));
        writer.close();
    }
}

class InitHandler extends DefaultHandler {
    private final Writer writer;

    public InitHandler(Writer writer) {
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
}
/**
 *  Add Catalonia XML data to the document
 * */
class CataloniaHandler extends DefaultHandler {
    private final Writer writer;
    private boolean isFirstCountry = true;

    public CataloniaHandler(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            if (qName.equals("mondial") && isFirstCountry) {
                writer.write("<mondial>\n");
                writer.write(E34a.CATALONIA_XML + "\n");
                isFirstCountry = false;
            } else {
                writer.write("<" + qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    writer.write(" " + attributes.getQName(i) + "=\"" + 
                               attributes.getValue(i) + "\"");
                }
                writer.write(">\n");
            }
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            writer.write("</" + qName + ">");
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            writer.write(new String(ch, start, length));
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
}

/**
 * Switch the province from Spain to Catalonia, like in E34a.
 */
class SwitchProvinceHandler extends DefaultHandler {
    private final Writer writer;

    public SwitchProvinceHandler(Writer writer) {
        this.writer = writer;
    }
}