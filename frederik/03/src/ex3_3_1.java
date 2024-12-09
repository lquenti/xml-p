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

public class ex3_3_1 {
    public static void saxCountryCityPopulation() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse("../../mondial.xml", new DefaultHandler() {
                private String currentCountry = null;
                private String currentCountryName = null;
                private List<Map<String, String>> currentCities = new ArrayList<>();

                private boolean isCity = false;
                private String currentCityName = null;
                private String currentCityPopulation = null;
                private String currentElement = null;

                private final Element htmlRoot = new Element("html");
                private final Element body = new Element("body");

                @Override
                public void startDocument() {
                    htmlRoot.addContent(body);
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    currentElement = qName;
                    if (qName.contains("country")) {
                        currentCountry = attributes.getValue("car_code");
                        currentCountryName = attributes.getValue("name");
                        currentCities = new ArrayList<>();
                    }

                    if (qName.contains("city")) {
                        isCity = true;
                        currentCityName = null;
                        currentCityPopulation = null;
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    String text = new String(ch, start, length).trim();

                    if (isCity && "name".equals(currentElement)) {
                        currentCityName = text;
                    } else if (isCity && "population".equals(currentElement)) {
                        currentCityPopulation = text;
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) {
                    if (qName.contains("city")) {
                        if (currentCityName != null) {
                            Map<String, String> cityData = new HashMap<>();
                            cityData.put("name", currentCityName);
                            cityData.put("population", currentCityPopulation != null ? currentCityPopulation : "N/A");
                            currentCities.add(cityData);
                        }
                        isCity = false;
                    }
                    if (qName.contains("country")) {
                        Element countryListItem = new Element("ul");
                        countryListItem.addContent(new Element("li").setText(currentCountryName != null ? currentCountryName : currentCountry));
                        Element table = new Element("table");
                        table.setAttribute("border", "1");

                        for (Map<String, String> city : currentCities) {
                            table.addContent(new Element("tr")
                                    .addContent(new Element("th").setText(city.get("name")))
                                    .addContent(new Element("th").setText(city.get("population"))));
                        }

                        countryListItem.addContent(table);
                        body.addContent(countryListItem);

                        currentCountry = null;
                        currentCountryName = null;
                        currentCities = new ArrayList<>();
                    }
                }

                @Override
                public void endDocument() {
                    try {
                        Document htmlDocument = new Document(htmlRoot);
                        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                        FileWriter writer = new FileWriter("country_cities.html");
                        outputter.output(htmlDocument, writer);
                        writer.close();
                        System.out.println("HTML output generated: country_cities.html");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        saxCountryCityPopulation();
    }
}
