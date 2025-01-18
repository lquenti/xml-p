package de.lquenti.reverseserver;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedWriter;
import java.io.IOException;

public class ReverseXMLHandler extends DefaultHandler {
    private final BufferedWriter writer;

    public ReverseXMLHandler(BufferedWriter writer) {
        this.writer = writer;
    }

    private String reverse(String input) {
        return new StringBuilder(input).reverse().toString();
    }

    @Override
    public void startDocument() {
        System.out.println("startDocument");
    }

    @Override
    public void endDocument() {
        try {
            // for easier curl debugging
            writer.write("\r\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("endDocument writer flush failed");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            String reversedElement = reverse(qName);
            writer.write("<" + reversedElement);
            System.out.println("Start Element: " + qName + " -> " + reversedElement);

            for (int i = 0; i < attributes.getLength(); i++) {
                String attrName = attributes.getQName(i);
                String reverseAttrName = reverse(attrName);
                String attrValue = attributes.getValue(i);
                writer.write(" " + reverseAttrName + "=\"" + attrValue + "\"");
                System.out.println("Attribute: " + attrName + " -> " + reverseAttrName + "=\"" + attrValue + "\"");
            }
            writer.write(">");
        } catch (IOException e) {
            System.err.println("startElement write failed");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            String reversedElement = reverse(qName);
            writer.write("<" + reversedElement + ">");
            System.out.println("End Element: " + qName + " -> " + reversedElement);
        } catch (IOException e) {
            System.err.println("endElement write failed");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        try {
            String text = new String(ch, start, length);
            writer.write(text);
            System.out.println("Text: " + text);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }
}