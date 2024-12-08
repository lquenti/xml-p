import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.util.*;

public class ex3_2 {

    public static void main(String[] args) {
        try {
            saxCountriesHTML();
            saxGermanyCapital();
            saxCountryTable();

            staxCountriesHTML();
            staxGermanyCapital();
            staxCountryTable();

            staxOrganizationQuery();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== SAX IMPLEMENTATIONS ==========

    // (a) Output HTML file listing names of all countries
    public static void saxCountriesHTML() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            FileWriter writer = new FileWriter("countries_sax.html");
            writer.write("<html><body><h1>Countries</h1><ul>");

            parser.parse("../../mondial.xml", new DefaultHandler() {

                ArrayList<String> currentValue = new ArrayList<>();

                @Override
                public void characters(char ch[], int start, int length) throws SAXException {
                    super.characters(ch, start, length);
                    currentValue.add(String.valueOf(ch).substring(start, start + length));
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    //System.out.println("-------uri: " + uri + "\t localName: "+ localName+"\t qName: "+  qName+"\t attributes: "+  attributes.toString()+"\t currentValue: "+  currentValue);
                    if (qName.equals("country")) {
                        currentValue.clear();
                    }
                }


                @Override
                public void endElement(String uri, String localName, String qName) {
                    if (qName.equals("country")) {
                        //System.out.println("-------uri: " + uri + "\t localName: "+ localName+"\t qName: "+  qName+"\t"+ "currentValue: "+  currentValue);
                        if (currentValue != null) {
                            try {
                                writer.write("<li>" + currentValue.get(0) + "</li>");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void endDocument() {
                    try {
                        writer.write("</ul></body></html>");
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saxGermanyCapital() throws XMLStreamException, FileNotFoundException {
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
                        System.out.println("Capital of Germany not found or missing population data.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // (c) Output all country names, their capital, and population as HTML table
    public static void saxCountryTable() throws ParserConfigurationException, SAXException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            parser.parse("../../mondial.xml", new DefaultHandler() {
                private boolean isCapitalCity = false;
                private String currentElement = null;

                private String countryCode = null;
                private String capitalId = null;
                private String capitalName = null;
                private String capitalPopulation = null;

                private final StringBuilder textOutput = new StringBuilder();
                private final Element htmlTable = new Element("html");

                @Override
                public void startDocument() throws SAXException {
                    super.startDocument();
                    Element body = new Element("body");
                    Element table = new Element("table");
                    table.setAttribute("border", "1");
                    table.addContent(new Element("tr")
                            .addContent(new Element("th").setText("CountryCode"))
                            .addContent(new Element("th").setText("Capital"))
                            .addContent(new Element("th").setText("Population")));
                    body.addContent(table);
                    htmlTable.addContent(body);
                }

                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    currentElement = qName;

                    if ("country".equals(qName)) {
                        countryCode = attributes.getValue("car_code");
                        capitalId = attributes.getValue("capital"); // Capture the capital ID
                    }

                    if ("city".equals(qName) && capitalId != null && capitalId.equals(attributes.getValue("id"))) {
                        isCapitalCity = true; // Start processing the capital city
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) throws SAXException {
                    if (isCapitalCity) {
                        String text = new String(ch, start, length).trim();

                        if ("name".equals(currentElement)) {
                            capitalName = text; // Capture the capital's name
                        } else if ("population".equals(currentElement)) {
                            capitalPopulation = text; // Capture the capital's population
                        }
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) throws SAXException {
                    if ("city".equals(qName)) {
                        isCapitalCity = false;
                    }

                    if ("country".equals(qName)) {
                        String populationDisplay = (capitalPopulation != null) ? capitalPopulation : "N/A";
                        textOutput.append(countryCode)
                                .append(" - ")
                                .append(capitalName != null ? capitalName : "No Capital")
                                .append(" - ")
                                .append(populationDisplay)
                                .append("\n");

                        System.out.println(countryCode + " - " +
                                (capitalName != null ? capitalName : "No Capital") +
                                " - " +
                                populationDisplay);

                        Element row = new Element("tr");
                        row.addContent(new Element("td").setText(countryCode));
                        row.addContent(new Element("td").setText(capitalName != null ? capitalName : "No Capital"));
                        row.addContent(new Element("td").setText(populationDisplay));
                        htmlTable.getChild("body").getChild("table").addContent(row);

                        capitalName = null;
                        capitalPopulation = null;
                    }

                    currentElement = null;
                }

                @Override
                public void endDocument() {
                    System.out.println("\n--- Text Output ---");
                    System.out.println(textOutput.toString());
                    writeHTMLTableToFile("countries_table_sax.html");
                }

                public void writeHTMLTableToFile(String fileName) {
                    try {
                        Document htmlDocument = new Document(htmlTable);
                        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
                        FileWriter writer = new FileWriter(fileName);
                        outputter.output(htmlDocument, writer);
                        writer.close();
                        System.out.println("HTML Table written to: " + fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void staxCountriesHTML() {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            FileInputStream inputStream = new FileInputStream("../../mondial.xml");
            XMLEventReader eventReader = factory.createXMLEventReader(inputStream);

            FileWriter writer = new FileWriter("countries_stax.html");
            writer.write("<html><body><h1>Countries</h1><ul>");

            StringBuilder currentText = new StringBuilder();
            boolean isInsideCountry = false;
            int nestedLevel = 0; // Tracks nesting level to handle sub-elements

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String tagName = startElement.getName().getLocalPart();

                    if ("country".equals(tagName)) {
                        isInsideCountry = true;
                        nestedLevel = 0;
                    } else if (isInsideCountry) {
                        nestedLevel++;
                        if ("name".equals(tagName) && nestedLevel == 1) {
                            currentText.setLength(0);
                        }
                    }
                } else if (event.isCharacters() && isInsideCountry && nestedLevel == 1) {
                    currentText.append(event.asCharacters().getData());
                } else if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    String tagName = endElement.getName().getLocalPart();

                    if ("name".equals(tagName) && isInsideCountry && nestedLevel == 1) {
                        String countryName = currentText.toString().trim();
                        if (!countryName.isEmpty()) {
                            writer.write("<li>" + countryName + "</li>");
                        }
                    } else if ("country".equals(tagName)) {
                        isInsideCountry = false;
                    } else if (isInsideCountry) {
                        nestedLevel--;
                    }
                }
            }

            writer.write("</ul></body></html>");
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void staxGermanyCapital() {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            FileInputStream inputStream = new FileInputStream("../../mondial.xml");
            XMLEventReader eventReader = factory.createXMLEventReader(inputStream);

            boolean isGermany = false;
            boolean isCapitalCity = false;
            String capitalId = null;
            String currentElement = null;
            String capitalName = null;
            String capitalPopulation = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String tagName = startElement.getName().getLocalPart();

                    if ("country".equals(tagName)) {
                        if ("D".equals(startElement.getAttributeByName(new QName("car_code")).getValue())) {
                            isGermany = true;
                            capitalId = startElement.getAttributeByName(new QName("capital")).getValue();
                        }
                    }

                    if (isGermany && "city".equals(tagName)) {
                        String cityId = startElement.getAttributeByName(new QName("id")).getValue();
                        if (capitalId != null && capitalId.equals(cityId)) {
                            isCapitalCity = true;
                        }
                    }

                    if (isCapitalCity) {
                        if ("name".equals(tagName)) {
                            currentElement = "name";
                        } else if ("population".equals(tagName)) {
                            currentElement = "population";
                        }
                    }
                }

                if (event.isCharacters()) {
                    Characters characters = event.asCharacters();
                    String text = characters.getData().trim();

                    if (isCapitalCity && !text.isEmpty()) {
                        if ("name".equals(currentElement)) {
                            capitalName = text;
                        } else if ("population".equals(currentElement)) {
                            capitalPopulation = text;
                        }
                    }
                }

                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    String tagName = endElement.getName().getLocalPart();

                    if ("city".equals(tagName)) {
                        isCapitalCity = false;
                    } else if ("country".equals(tagName)) {
                        isGermany = false;
                    }

                    currentElement = null;
                }
            }

            if (capitalName != null && capitalPopulation != null) {
                System.out.println("Capital of Germany: " + capitalName);
                System.out.println("Population: " + capitalPopulation);
            } else {
                System.out.println("Capital of Germany not found or missing population data.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void staxCountryTable() {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            FileInputStream inputStream = new FileInputStream("../../mondial.xml");
            XMLEventReader eventReader = factory.createXMLEventReader(inputStream);

            boolean isCapitalCity = false;
            String currentElement = null;

            String countryCode = null;
            String capitalId = null;
            String capitalName = null;
            String capitalPopulation = null;

            final StringBuilder textOutput = new StringBuilder();
            Element htmlTable = new Element("html");
            Element body = new Element("body");
            Element table = new Element("table").setAttribute("border", "1");
            table.addContent(new Element("tr")
                    .addContent(new Element("th").setText("CountryCode"))
                    .addContent(new Element("th").setText("Capital"))
                    .addContent(new Element("th").setText("Population")));
            body.addContent(table);
            htmlTable.addContent(body);

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String tagName = startElement.getName().getLocalPart();

                    if ("country".equals(tagName)) {
                        countryCode = startElement.getAttributeByName(new QName("car_code")).getValue();
                        capitalId = startElement.getAttributeByName(new QName("capital")).getValue();
                    }

                    if ("city".equals(tagName) && capitalId != null && capitalId.equals(startElement.getAttributeByName(new QName("id")).getValue())) {
                        isCapitalCity = true;
                    }

                    if (isCapitalCity) {
                        if ("name".equals(tagName)) {
                            currentElement = "name";
                        } else if ("population".equals(tagName)) {
                            currentElement = "population";
                        }
                    }
                }

                if (event.isCharacters()) {
                    Characters characters = event.asCharacters();
                    String text = characters.getData().trim();

                    if (isCapitalCity && !text.isEmpty()) {
                        if ("name".equals(currentElement)) {
                            capitalName = text;
                        } else if ("population".equals(currentElement)) {
                            capitalPopulation = text;
                        }
                    }
                }

                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    String tagName = endElement.getName().getLocalPart();

                    if ("city".equals(tagName)) {
                        isCapitalCity = false;
                    } else if ("country".equals(tagName)) {
                        String populationDisplay = (capitalPopulation != null) ? capitalPopulation : "N/A";
                        textOutput.append(countryCode)
                                .append(" - ")
                                .append(capitalName != null ? capitalName : "No Capital")
                                .append(" - ")
                                .append(populationDisplay)
                                .append("\n");

                        System.out.println(countryCode + " - " +
                                (capitalName != null ? capitalName : "No Capital") +
                                " - " +
                                populationDisplay);

                        Element row = new Element("tr");
                        row.addContent(new Element("td").setText(countryCode));
                        row.addContent(new Element("td").setText(capitalName != null ? capitalName : "No Capital"));
                        row.addContent(new Element("td").setText(populationDisplay));
                        table.addContent(row);

                        capitalName = null;
                        capitalPopulation = null;
                    }

                    currentElement = null;
                }
            }

            System.out.println("\n--- Text Output ---");
            System.out.println(textOutput);

            writeHTMLTableToFile(htmlTable, "countries_table_stax.html");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeHTMLTableToFile(Element htmlTable, String fileName) {
        try {
            Document htmlDocument = new Document(htmlTable);
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            FileWriter writer = new FileWriter(fileName);
            outputter.output(htmlDocument, writer);
            writer.close();
            System.out.println("HTML Table written to: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void staxOrganizationQuery() {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            FileInputStream inputStream = new FileInputStream("../../mondial.xml");
            XMLEventReader  eventReader = factory.createXMLEventReader(inputStream);

            // Map to store country information: key = country code, value = [capital ID, capital name]
            Map<String, String[]> countryMap = new HashMap<>();

            String currentCountryCode = null;
            String currentCapitalId = null;
            String currentCityId = null;
            String currentCityName = null;
            boolean isInCity = false;

            boolean isInOrganization = false;
            String organizationName = null;
            String organizationMembers = null;

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String tagName = startElement.getName().getLocalPart();

                    if ("country".equals(tagName)) {
                        currentCountryCode = startElement.getAttributeByName(new QName("car_code")).getValue();
                        currentCapitalId = startElement.getAttributeByName(new QName("capital")).getValue();
                    }

                    if ("city".equals(tagName)) {
                        currentCityId = startElement.getAttributeByName(new QName("id")).getValue();
                        isInCity = true;
                    }

                    if ("organization".equals(tagName)) {
                        isInOrganization = true;
                        organizationName = null;
                        organizationMembers = null;
                    }

                    if ("members".equals(tagName) && isInOrganization) {
                        organizationMembers = startElement.getAttributeByName(new QName("country")).getValue();
                    }
                }

                if (event.isCharacters()) {
                    Characters characters = event.asCharacters();
                    String text = characters.getData().trim();

                    if (isInCity && !text.isEmpty()) {
                        if(currentCityName == null){
                            currentCityName = text;
                        }

                    }

                    if (isInOrganization && organizationName == null && !text.isEmpty()) {
                        organizationName = text;
                    }
                }

                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    String tagName = endElement.getName().getLocalPart();

                    if ("city".equals(tagName)) {
                        if (currentCityId != null && currentCityId.equals(currentCapitalId)) {
                            countryMap.put(currentCountryCode, new String[]{currentCityId, currentCityName});
                        }
                        isInCity = false;
                        currentCityId = null;
                        currentCityName = null;
                    }

                    if ("organization".equals(tagName) && isInOrganization) {
                        if (organizationMembers != null) {
                            String[] memberCodes = organizationMembers.split(" ");
                            for (String memberCode : memberCodes) {
                                if (countryMap.containsKey(memberCode)) {
                                    String[] capitalInfo = countryMap.get(memberCode);
                                    String capitalName = capitalInfo[1];
                                    System.out.println("Organization: " + organizationName + ", Headquarter City: " + capitalName);
                                    break;
                                }
                            }
                        }
                        isInOrganization = false;
                    }

                    if ("country".equals(tagName)) {
                        currentCountryCode = null;
                        currentCapitalId = null;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

