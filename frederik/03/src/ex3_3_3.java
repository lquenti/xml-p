import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ex3_3_3 {
    public static void saxGermanyCapitalStopAfterOutput() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse("../../mondial.xml", new DefaultHandler() {

                private boolean isGermany = false;
                private boolean isCapitalCity = false;
                private String capitalId = null;
                private String currentElement = null;
                private String capitalName = null;
                private String capitalPopulation = null;

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    currentElement = qName;

                    if ("country".equals(qName) && "D".equals(attributes.getValue("car_code"))) {
                        isGermany = true;
                        capitalId = attributes.getValue("capital");
                    }

                    if (isGermany && "city".equals(qName) && capitalId != null && capitalId.equals(attributes.getValue("id"))) {
                        isCapitalCity = true;
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    if (isCapitalCity) {
                        String text = new String(ch, start, length).trim();

                        if ("name".equals(currentElement)) {
                            capitalName = text;
                            System.out.println("capitalName: " + capitalName);
                        } else if ("population".equals(currentElement)) {
                            capitalPopulation = text;
                            System.out.println("capitalPopulation: " + capitalPopulation);
                            System.out.println("ch: " + new String(ch));
                            System.exit(0);
                            /*
                             * The characters(char[], int, int) method provides a segment of the XML's text content.
                             * start and length define which part of the char[] is valid.
                             * The content may include whitespace or incomplete text if the data spans multiple chunks.
                             * SAX parsers may call characters() multiple times for the same text node, just with different start and length.
                             */
                        }
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) {
                    if ("city".equals(qName)) {
                        isCapitalCity = false;
                    }
                    currentElement = null;

                    if ("country".equals(qName)) {
                        isGermany = false;
                    }
                }

                @Override
                public void endDocument() {
                    if (capitalName != null && capitalPopulation != null) {
                        System.out.println("Capital of Germany: " + capitalName);
                        System.out.println("Population: " + capitalPopulation);
                    } else {
                        System.out.println("Capital of Germany not found or missing population data.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        saxGermanyCapitalStopAfterOutput();
    }
}
