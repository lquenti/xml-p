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

        reader.setContentHandler(new E36aHandler(writer));
        reader.parse(new InputSource("mondial.xml"));
        writer.close();
    }
}

class E36aHandler extends DefaultHandler {
    private final Writer writer;

    public E36aHandler(Writer writer) {
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
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

    }
}