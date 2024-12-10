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

                private String currentCountry = null;
                private String currentCountryName = null;
                private List<Map<String, String>> currentCities = new ArrayList<>();
                private String capitalId = null;

                private boolean isCity = false;
                private String currentCityId = null;
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

                    if ("country".equals(qName)) {
                        currentCountry = attributes.getValue("car_code");
                        currentCountryName = attributes.getValue("name");
                        capitalId = attributes.getValue("capital");
                        currentCities = new ArrayList<>();
                    }

                    if ("city".equals(qName)) {
                        isCity = true;
                        currentCityId = attributes.getValue("id");
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
                    if ("city".equals(qName)) {
                        if (currentCityName != null) {
                            Map<String, String> cityData = new HashMap<>();
                            cityData.put("id", currentCityId);
                            cityData.put("name", currentCityName);
                            cityData.put("population", currentCityPopulation != null ? currentCityPopulation : "N/A");
                            currentCities.add(cityData);
                        }
                        isCity = false;
                    }

                    if ("country".equals(qName)) {
                        List<Map<String, String>> validCities = new ArrayList<>();
                        long totalPopulation = 0;

                        for (Map<String, String> city : currentCities) {
                            if (!city.get("population").equals("N/A")) {
                                validCities.add(city);
                                totalPopulation += Long.parseLong(city.get("population"));
                            }
                        }

                        if (validCities.size() >= 10) {
                            long averagePopulation = totalPopulation / validCities.size();

                            Map<String, String> closestCity = null;
                            long minDiff = Long.MAX_VALUE;
                            for (Map<String, String> city : validCities) {
                                long population = Long.parseLong(city.get("population"));
                                long diff = Math.abs(population - averagePopulation);
                                if (diff < minDiff) {
                                    closestCity = city;
                                    minDiff = diff;
                                }
                            }

                            Element countrySection = new Element("div");
                            countrySection.addContent(new Element("h2").setText(currentCountryName != null ? currentCountryName : currentCountry));
                            countrySection.addContent(new Element("p").setText("Total cities: " + validCities.size()));
                            countrySection.addContent(new Element("p").setText("Average city population: " + averagePopulation));

                            Element cityTable = new Element("table");
                            cityTable.setAttribute("border", "1");
                            cityTable.addContent(new Element("tr")
                                    .addContent(new Element("th").setText("City"))
                                    .addContent(new Element("th").setText("Population")));

                            for (Map<String, String> city : validCities) {
                                Element cityRow = new Element("tr");

                                if (city.get("id").equals(capitalId)) {
                                    cityRow.setAttribute("style", "color: blue;");
                                } else if (city.equals(closestCity)) {
                                    cityRow.setAttribute("style", "font-style: italic;");
                                }

                                cityRow.addContent(new Element("td").setText(city.get("name")));
                                cityRow.addContent(new Element("td").setText(city.get("population")));
                                cityTable.addContent(cityRow);
                            }

                            countrySection.addContent(cityTable);
                            body.addContent(countrySection);
                        }

                        // Reset for the next country
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
                        FileWriter writer = new FileWriter("country_cities_with_stats.html");
                        outputter.output(htmlDocument, writer);
                        writer.close();
                        System.out.println("HTML output generated: country_cities_with_stats.html");
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
        saxCountryCityPopulationWithStats();
    }
}
