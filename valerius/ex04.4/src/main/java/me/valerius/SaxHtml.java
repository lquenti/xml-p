package me.valerius;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxHtml {
    public static void parseAndTransform(String inputFile, String outputFile) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);

            SAXParser parser = factory.newSAXParser();

            try (Writer writer = new BufferedWriter(new FileWriter(outputFile))) {
                HtmlHandler handler = new HtmlHandler(writer);
                parser.parse(new File(inputFile), handler);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class HtmlHandler extends DefaultHandler {
        private final Writer writer;
        private boolean skipContent = false;

        public HtmlHandler(Writer writer) {
            this.writer = writer;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            try {
                String elementName = qName.toLowerCase();
                if (elementName.equals("style") || elementName.equals("script")) {
                    skipContent = true;
                    return;
                }

                if (!skipContent) {
                    writer.write("<" + qName);
                    // Write attributes
                    for (int i = 0; i < attributes.getLength(); i++) {
                        writer.write(" " + attributes.getQName(i) + "=\"" +
                                escapeXml(attributes.getValue(i)) + "\"");
                    }
                    writer.write(">");
                }
            } catch (IOException e) {
                throw new SAXException(e);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            try {
                String elementName = qName.toLowerCase();
                if (elementName.equals("style") || elementName.equals("script")) {
                    skipContent = false;
                    return;
                }

                if (!skipContent) {
                    writer.write("</" + qName + ">");
                }
            } catch (IOException e) {
                throw new SAXException(e);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (!skipContent) {
                try {
                    String content = new String(ch, start, length);
                    writer.write(escapeXml(content));
                } catch (IOException e) {
                    throw new SAXException(e);
                }
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

    public static void main(String[] args) {
        parseAndTransform("montblanc.html", "montblanc_sax.html");
    }
}