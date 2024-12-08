import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ex3_3_2 {
    public static void saxCountryCityPopulationWithStats() {
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
                        } else if ("population".equals(currentElement)) {
                            capitalPopulation = text;
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
                        System.out.println("Capital of Germany not found");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        saxCountryCityPopulationWithStats();
    }
}
