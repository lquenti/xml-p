package me.valerius.stax;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class CataloniaStAX {
    public static final String SPAIN_CAR_CODE = "E";
    public static final String PROVINCE_TO_DETACH = "prov-Spain-11";

    public static void main(String[] args) throws XMLStreamException, FileNotFoundException {
        XMLProcessor processor = new XMLProcessor();
        processor.process();
    }
}

class XMLProcessor {
    private final XMLReader xmlReader;
    private final ProvinceHandler provinceHandler;
    private final CataloniaWriter cataloniaWriter;
    private boolean isSpain = false;
    private String spainMemberships = null;
    private boolean skipProvince = false;
    private boolean isRiverWithCatalonianProvince = false;

    public XMLProcessor() {
        this.xmlReader = new XMLReader();
        this.provinceHandler = new ProvinceHandler();
        this.cataloniaWriter = new CataloniaWriter();
    }

    public void process() throws XMLStreamException, FileNotFoundException {
        String mondialData = xmlReader.getMondialData();

        XMLEventReader reader = xmlReader.createReader(mondialData);
        XMLStreamWriter writer = xmlReader.createWriter("output_stax.xml");

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();

                // Reset flag when starting a new river
                if (elementName.equals("river")) {
                    isRiverWithCatalonianProvince = false;
                }

                processStartElement(event, writer, null);
            } else if (event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                String elementName = endElement.getName().getLocalPart();

                if (skipProvince) {
                    provinceHandler.appendEndElement(elementName);
                    if (elementName.equals("province")) {
                        skipProvince = false;
                    }
                } else {
                    writer.writeEndElement();
                }

                if (elementName.equals("country") && isSpain) {
                    isSpain = false;
                    if (provinceHandler.hasCapturedProvince()) {
                        cataloniaWriter.writeCatalonia(writer, provinceHandler.getCapturedProvince(), spainMemberships);
                    }
                }
            } else if (event.isCharacters()) {
                Characters characters = event.asCharacters();
                if (skipProvince) {
                    provinceHandler.appendCharacters(characters.getData());
                } else {
                    writer.writeCharacters(characters.getData());
                }
            }
        }

        writer.writeEndDocument();
        writer.flush();
        writer.close();
    }

    private void processStartElement(XMLEvent event, XMLStreamWriter writer, StringBuilder capturedProvince)
            throws XMLStreamException {
        StartElement startElement = event.asStartElement();
        String elementName = startElement.getName().getLocalPart();

        if (elementName.equals("country")) {
            processCountryElement(startElement, writer);
        } else if (elementName.equals("province")) {
            processProvinceElement(startElement, writer);
        } else if (elementName.equals("sea")) {
            processSeaElement(startElement, writer);
        } else if (elementName.equals("river") || elementName.equals("source")) {
            processRiverOrSourceElement(startElement, writer);
        } else if (elementName.equals("estuary")) {
            processEstuaryElement(startElement, writer);
        } else {
            processOtherElement(startElement, writer);
        }
    }

    private void processCountryElement(StartElement startElement, XMLStreamWriter writer) throws XMLStreamException {
        if (startElement.getAttributeByName(new javax.xml.namespace.QName("car_code")).getValue()
                .equals(CataloniaStAX.SPAIN_CAR_CODE)) {
            isSpain = true;
            Attribute membershipsAttr = startElement.getAttributeByName(new javax.xml.namespace.QName("memberships"));
            if (membershipsAttr != null) {
                spainMemberships = membershipsAttr.getValue();
            }
        }
        writeStartElementWithAttributes(writer, startElement);
    }

    private void processProvinceElement(StartElement startElement, XMLStreamWriter writer) throws XMLStreamException {
        if (isSpain && startElement.getAttributeByName(new javax.xml.namespace.QName("id")).getValue()
                .equals(CataloniaStAX.PROVINCE_TO_DETACH)) {
            skipProvince = true;
            provinceHandler.startNewProvince(startElement);
        } else {
            writeStartElementWithAttributes(writer, startElement);
        }
    }

    private void processSeaElement(StartElement startElement, XMLStreamWriter writer) throws XMLStreamException {
        String id = startElement.getAttributeByName(new javax.xml.namespace.QName("id")).getValue();
        if (id.equals("sea-Mittelmeer")) {
            // Add CAT to the country list
            String countries = startElement.getAttributeByName(new javax.xml.namespace.QName("country")).getValue();
            String newCountries = "CAT " + countries;

            writer.writeStartElement("sea");
            writer.writeAttribute("country", newCountries);
            writer.writeAttribute("id", id);

            // Copy other attributes
            for (Iterator<Attribute> it = startElement.getAttributes(); it.hasNext();) {
                Attribute attribute = it.next();
                String attrName = attribute.getName().getLocalPart();
                if (!attrName.equals("country") && !attrName.equals("id")) {
                    writer.writeAttribute(attrName, attribute.getValue());
                }
            }
        } else {
            writeStartElementWithAttributes(writer, startElement);
        }
    }

    private void processRiverOrSourceElement(StartElement startElement, XMLStreamWriter writer)
            throws XMLStreamException {
        String elementName = startElement.getName().getLocalPart();
        Attribute countryAttr = startElement.getAttributeByName(new javax.xml.namespace.QName("country"));

        writer.writeStartElement(elementName);

        if (countryAttr != null) {
            String countries = countryAttr.getValue();
            if (elementName.equals("river") && countries.contains("E")) {
                // Replace E with CAT in the country list while preserving other countries
                String[] countryList = countries.split(" ");
                for (int i = 0; i < countryList.length; i++) {
                    if (countryList[i].equals("E")) {
                        countryList[i] = "CAT";
                    }
                }
                writer.writeAttribute("country", String.join(" ", countryList));
            } else if (elementName.equals("source") && isRiverWithCatalonianProvince && countries.equals("E")) {
                // Only replace source country if it's Spain and this river involves Catalonia
                writer.writeAttribute("country", "CAT");
            } else {
                writer.writeAttribute("country", countries);
            }
        }

        // Write other attributes
        for (Iterator<Attribute> it = startElement.getAttributes(); it.hasNext();) {
            Attribute attribute = it.next();
            if (!attribute.getName().getLocalPart().equals("country")) {
                writer.writeAttribute(attribute.getName().getLocalPart(), attribute.getValue());
            }
        }
    }

    private void processEstuaryElement(StartElement startElement, XMLStreamWriter writer) throws XMLStreamException {
        Attribute countryAttr = startElement.getAttributeByName(new javax.xml.namespace.QName("country"));

        writer.writeStartElement("estuary");

        if (countryAttr != null) {
            String country = countryAttr.getValue();
            if (country.equals("E") && isRiverWithCatalonianProvince) {
                writer.writeAttribute("country", "CAT");
            } else {
                writer.writeAttribute("country", country);
            }
        }
    }

    private void processOtherElement(StartElement startElement, XMLStreamWriter writer) throws XMLStreamException {
        String elementName = startElement.getName().getLocalPart();
        if (skipProvince) {
            provinceHandler.appendStartElement(startElement);
        } else if (elementName.equals("located")) {
            processLocatedElement(startElement, writer);
        } else {
            writeStartElementWithAttributes(writer, startElement);
        }
    }

    private void processLocatedElement(StartElement startElement, XMLStreamWriter writer) throws XMLStreamException {
        String country = startElement.getAttributeByName(new javax.xml.namespace.QName("country")).getValue();
        String province = startElement.getAttributeByName(new javax.xml.namespace.QName("province")).getValue();

        // If this is Spain's province that should be Catalonia's
        if ((country.equals("E") || country.equals("CAT")) && province.equals(CataloniaStAX.PROVINCE_TO_DETACH)) {
            isRiverWithCatalonianProvince = true;
            writer.writeStartElement("located");
            writer.writeAttribute("country", "CAT");
            writer.writeAttribute("province", province);
        } else {
            writer.writeStartElement("located");
            writer.writeAttribute("country", country);
            writer.writeAttribute("province", province);
        }
    }

    private void writeStartElementWithAttributes(XMLStreamWriter writer, StartElement startElement)
            throws XMLStreamException {
        writer.writeStartElement(startElement.getName().getLocalPart());
        for (Iterator<Attribute> it = startElement.getAttributes(); it.hasNext();) {
            Attribute attribute = it.next();
            writer.writeAttribute(attribute.getName().getLocalPart(), attribute.getValue());
        }
    }
}

class XMLReader {
    public String getMondialData() {
        File file = new File("mondial.xml");
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCataloniaData() {
        File file = new File("catdata.xml");
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public XMLEventReader createReader(String data) throws XMLStreamException {
        assert data != null;
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        return xmlInputFactory.createXMLEventReader(new StringReader(data));
    }

    public XMLStreamWriter createWriter(String outputFile) throws XMLStreamException, FileNotFoundException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(new FileOutputStream(outputFile));
        writer.writeStartDocument("utf-8", "1.0");
        writer.writeDTD("<!DOCTYPE mondial SYSTEM \"mondial.dtd\">");
        return writer;
    }
}

class ProvinceHandler {
    private StringBuilder capturedProvince;

    public void startNewProvince(StartElement startElement) {
        capturedProvince = new StringBuilder();
        capturedProvince.append("<province");
        appendAttributes(startElement);
        capturedProvince.append(">");
    }

    public boolean hasCapturedProvince() {
        return capturedProvince != null;
    }

    public void appendStartElement(StartElement startElement) {
        capturedProvince.append("<").append(startElement.getName().getLocalPart());
        appendAttributes(startElement);
        capturedProvince.append(">");
    }

    private void appendAttributes(StartElement startElement) {
        for (Iterator<Attribute> it = startElement.getAttributes(); it.hasNext();) {
            Attribute attribute = it.next();
            capturedProvince.append(" ").append(attribute.getName().getLocalPart())
                    .append("=\"").append(attribute.getValue()).append("\"");
        }
    }

    public void appendEndElement(String elementName) {
        capturedProvince.append("</").append(elementName).append(">");
    }

    public void appendCharacters(String data) {
        capturedProvince.append(data);
    }

    public String getCapturedProvince() {
        return capturedProvince.toString();
    }
}

class CataloniaWriter {
    private final XMLReader xmlReader;

    public CataloniaWriter() {
        this.xmlReader = new XMLReader();
    }

    public void writeCatalonia(XMLStreamWriter writer, String capturedProvince, String memberships)
            throws XMLStreamException {
        writer.flush();

        XMLEventReader reader = xmlReader.createReader(xmlReader.getCataloniaData());
        XMLEventReader capturedProvinceReader = xmlReader.createReader(capturedProvince);

        ProvinceDataExtractor extractor = new ProvinceDataExtractor(capturedProvince);
        String areaValue = extractor.extractArea();
        String populationElements = extractor.extractPopulation();

        writeCataloniaContent(writer, reader, capturedProvinceReader, areaValue, memberships, populationElements);
    }

    private void writeCataloniaContent(XMLStreamWriter writer, XMLEventReader reader,
            XMLEventReader capturedProvinceReader,
            String areaValue, String memberships, String populationElements) throws XMLStreamException {

        Map<Integer, String> stringMap = new HashMap<>();
        String[] populationElementsArray = populationElements.split("</population>");
        for (String element : populationElementsArray) {
            String[] elements = element.split("\"");
            Integer k = Integer.parseInt(elements[3]);
            stringMap.put(k, element + "</population>");
        }
        // recompose the population elements
        StringBuilder sb = new StringBuilder();
        for (Integer k : stringMap.keySet()) {
            sb.append(stringMap.get(k));
        }
        populationElements = sb.toString();
        // Write start of country
        writeStartCountry(writer, areaValue, memberships);

        boolean afterName = false;
        boolean populationsWritten = false;

        // Read and write elements from catdata.xml
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();

                // Skip the root country element
                if (elementName.equals("country"))
                    continue;

                writer.writeStartElement(elementName);
                for (Iterator<Attribute> it = startElement.getAttributes(); it.hasNext();) {
                    Attribute attribute = it.next();
                    writer.writeAttribute(attribute.getName().getLocalPart(), attribute.getValue());
                }

                if (elementName.equals("name")) {
                    afterName = true;
                }
            } else if (event.isEndElement()) {
                String elementName = event.asEndElement().getName().getLocalPart();
                if (!elementName.equals("country")) {
                    writer.writeEndElement();

                    // After writing the name end tag, write population elements
                    if (elementName.equals("name") && afterName && !populationsWritten) {
                        writePopulationElements(writer, populationElements);
                        populationsWritten = true;
                        afterName = false;
                    }
                }
            } else if (event.isCharacters()) {
                writer.writeCharacters(event.asCharacters().getData());
            }
        }

        // Write province at the end
        writeProvince(writer, capturedProvinceReader);

        writer.writeEndElement(); // end country
        writer.flush();
    }

    private void writePopulationElements(XMLStreamWriter writer, String populationElements) throws XMLStreamException {
        XMLEventReader populationReader = xmlReader.createReader("<root>" + populationElements + "</root>");
        while (populationReader.hasNext()) {
            XMLEvent event = populationReader.nextEvent();
            if (event.isStartElement() && !event.asStartElement().getName().getLocalPart().equals("root")) {
                StartElement startElement = event.asStartElement();
                writer.writeStartElement(startElement.getName().getLocalPart());
                for (Iterator<Attribute> it = startElement.getAttributes(); it.hasNext();) {
                    Attribute attribute = it.next();
                    writer.writeAttribute(attribute.getName().getLocalPart(), attribute.getValue());
                }
            } else if (event.isCharacters()) {
                writer.writeCharacters(event.asCharacters().getData());
            } else if (event.isEndElement() && !event.asEndElement().getName().getLocalPart().equals("root")) {
                writer.writeEndElement();
            }
        }
    }

    private void writeStartCountry(XMLStreamWriter writer, String areaValue, String memberships)
            throws XMLStreamException {
        writer.writeStartElement("country");
        writer.writeAttribute("car_code", "CAT");
        if (areaValue != null) {
            writer.writeAttribute("area", areaValue);
        }
        if (memberships != null) {
            writer.writeAttribute("memberships", memberships);
        }
    }

    private void writeProvince(XMLStreamWriter writer, XMLEventReader capturedProvinceReader)
            throws XMLStreamException {
        while (capturedProvinceReader.hasNext()) {
            XMLEvent capturedEvent = capturedProvinceReader.nextEvent();
            if (capturedEvent.isStartElement()) {
                StartElement capturedStartElement = capturedEvent.asStartElement();
                writer.writeStartElement(capturedStartElement.getName().getLocalPart());
                writeProvinceAttributes(writer, capturedStartElement);
            } else if (capturedEvent.isEndElement()) {
                writer.writeEndElement();
            } else if (capturedEvent.isCharacters()) {
                writer.writeCharacters(capturedEvent.asCharacters().getData());
            }
        }
    }

    private void writeProvinceAttributes(XMLStreamWriter writer, StartElement startElement) throws XMLStreamException {
        for (Iterator<Attribute> it = startElement.getAttributes(); it.hasNext();) {
            Attribute attribute = it.next();
            String attrName = attribute.getName().getLocalPart();
            String attrValue = attribute.getValue();
            if (attrName.equals("country") && attrValue.equals("E")) {
                attrValue = "CAT";
            }
            writer.writeAttribute(attrName, attrValue);
        }
    }
}

class ProvinceDataExtractor {
    private final String provinceData;

    public ProvinceDataExtractor(String provinceData) {
        this.provinceData = provinceData;
    }

    public String extractArea() throws XMLStreamException {
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(new StringReader(provinceData));
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("area")) {
                event = reader.nextEvent();
                if (event.isCharacters()) {
                    return event.asCharacters().getData();
                }
            }
        }
        return null;
    }

    public String extractPopulation() throws XMLStreamException {
        StringBuilder populations = new StringBuilder();
        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(new StringReader(provinceData));
        boolean inPopulation = false;

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("population")) {
                inPopulation = true;
                populations.append("<population");
                StartElement startElement = event.asStartElement();
                for (Iterator<Attribute> it = startElement.getAttributes(); it.hasNext();) {
                    Attribute attribute = it.next();
                    populations.append(" ").append(attribute.getName().getLocalPart())
                            .append("=\"").append(attribute.getValue()).append("\"");
                }
                populations.append(">");
            } else if (event.isCharacters() && inPopulation) {
                populations.append(event.asCharacters().getData());
            } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("population")) {
                populations.append("</population>");
                inPopulation = false;
            }
        }
        return populations.toString();
    }
}
