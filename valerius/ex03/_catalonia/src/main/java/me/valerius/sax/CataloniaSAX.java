package me.valerius.sax;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public class CataloniaSAX {
    public static final String SPAIN_CAR_CODE = "E";
    public static final String PROVINCE_TO_DETACH = "prov-Spain-11";

    public static void main(String[] args) {
        try {
            XMLProcessor processor = new XMLProcessor();
            processor.process();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class XMLProcessor {
    private final XMLReader xmlReader;
    private final ProvinceHandler provinceHandler;
    private final CataloniaWriter cataloniaWriter;

    public XMLProcessor() {
        this.xmlReader = new XMLReader();
        this.provinceHandler = new ProvinceHandler();
        this.cataloniaWriter = new CataloniaWriter();
    }

    public void process() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        // Setup the output
        StreamResult streamResult = new StreamResult(new File("output_sax.xml"));
        SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler handler = tf.newTransformerHandler();
        Transformer serializer = handler.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "mondial.dtd");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        handler.setResult(streamResult);

        MondialHandler mondialHandler = new MondialHandler(handler, provinceHandler, cataloniaWriter);
        saxParser.parse(new File("mondial.xml"), mondialHandler);
    }
}

class MondialHandler extends DefaultHandler {
    private final TransformerHandler handler;
    private final ProvinceHandler provinceHandler;
    private final CataloniaWriter cataloniaWriter;
    private boolean isSpain = false;
    private String spainMemberships = null;
    private boolean skipProvince = false;
    private boolean isRiverWithCatalonianProvince = false;
    private StringBuilder characters = new StringBuilder();
    private String capturedProvince = null;
    private boolean inMondial = false;

    public MondialHandler(TransformerHandler handler, ProvinceHandler provinceHandler,
            CataloniaWriter cataloniaWriter) {
        this.handler = handler;
        this.provinceHandler = provinceHandler;
        this.cataloniaWriter = cataloniaWriter;
    }

    @Override
    public void startDocument() throws SAXException {
        handler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        handler.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("mondial")) {
            inMondial = true;
        }
        characters.setLength(0);

        if (qName.equals("river")) {
            isRiverWithCatalonianProvince = false;
        }

        if (skipProvince) {
            provinceHandler.startElement(qName, attributes);
            return;
        }

        if (qName.equals("country")) {
            processCountryStart(attributes);
        } else if (qName.equals("province")) {
            processProvinceStart(qName, attributes);
        } else if (qName.equals("sea")) {
            processSeaStart(qName, attributes);
        } else if (qName.equals("river") || qName.equals("source")) {
            processRiverOrSourceStart(qName, attributes);
        } else if (qName.equals("estuary")) {
            processEstuaryStart(qName, attributes);
        } else if (qName.equals("located")) {
            processLocatedStart(qName, attributes);
        } else {
            handler.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (skipProvince) {
            provinceHandler.endElement(qName);
            if (qName.equals("province")) {
                skipProvince = false;
            }
            return;
        }

        handler.endElement(uri, localName, qName);

        if (qName.equals("country") && isSpain) {
            isSpain = false;
            if (provinceHandler.hasCapturedProvince()) {
                cataloniaWriter.writeCatalonia(handler, provinceHandler.getCapturedProvince(), spainMemberships);
            }
        }

        characters.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (skipProvince) {
            provinceHandler.characters(ch, start, length);
        } else {
            String content = new String(ch, start, length);
            characters.append(content);
            handler.characters(ch, start, length);
        }
    }

    private void processCountryStart(Attributes attributes) throws SAXException {
        String carCode = attributes.getValue("car_code");
        if (carCode != null && carCode.equals(CataloniaSAX.SPAIN_CAR_CODE)) {
            isSpain = true;
            spainMemberships = attributes.getValue("memberships");
        }
        handler.startElement("", "", "country", attributes);
    }

    private void processProvinceStart(String qName, Attributes attributes) throws SAXException {
        if (isSpain && attributes.getValue("id").equals(CataloniaSAX.PROVINCE_TO_DETACH)) {
            skipProvince = true;
            provinceHandler.startElement(qName, attributes);
        } else {
            handler.startElement("", "", qName, attributes);
        }
    }

    private void processSeaStart(String qName, Attributes attributes) throws SAXException {
        String id = attributes.getValue("id");
        if (id.equals("sea-Mittelmeer")) { // TODO:
            AttributesImpl newAttrs = new AttributesImpl(attributes);
            String countries = attributes.getValue("country");
            String newCountries = "CAT " + countries;
            updateAttribute(newAttrs, "country", newCountries);
            handler.startElement("", "", qName, newAttrs);
        } else {
            handler.startElement("", "", qName, attributes);
        }
    }

    private void processRiverOrSourceStart(String qName, Attributes attributes) throws SAXException {
        String countries = attributes.getValue("country");
        if (countries != null) {
            AttributesImpl newAttrs = new AttributesImpl(attributes);
            if (qName.equals("river") && countries.contains("E")) {
                String[] countryList = countries.split(" ");
                for (int i = 0; i < countryList.length; i++) {
                    if (countryList[i].equals("E")) {
                        countryList[i] = "CAT";
                    }
                }
                updateAttribute(newAttrs, "country", String.join(" ", countryList));
            } else if (qName.equals("source") && isRiverWithCatalonianProvince && countries.equals("E")) {
                updateAttribute(newAttrs, "country", "CAT");
            }
            handler.startElement("", "", qName, newAttrs);
        } else {
            handler.startElement("", "", qName, attributes);
        }
    }

    private void processEstuaryStart(String qName, Attributes attributes) throws SAXException {
        String country = attributes.getValue("country");
        if (country != null && country.equals("E") && isRiverWithCatalonianProvince) {
            AttributesImpl newAttrs = new AttributesImpl(attributes);
            updateAttribute(newAttrs, "country", "CAT");
            handler.startElement("", "", qName, newAttrs);
        } else {
            handler.startElement("", "", qName, attributes);
        }
    }

    private void processLocatedStart(String qName, Attributes attributes) throws SAXException {
        String country = attributes.getValue("country");
        String province = attributes.getValue("province");

        if ((country.equals("E") || country.equals("CAT")) && province.equals(CataloniaSAX.PROVINCE_TO_DETACH)) {
            isRiverWithCatalonianProvince = true;
            AttributesImpl newAttrs = new AttributesImpl(attributes);
            updateAttribute(newAttrs, "country", "CAT");
            handler.startElement("", "", qName, newAttrs);
        } else {
            handler.startElement("", "", qName, attributes);
        }
    }

    private void updateAttribute(AttributesImpl attrs, String name, String value) {
        int index = attrs.getIndex(name);
        if (index >= 0) {
            attrs.setAttribute(index, "", name, name, "CDATA", value);
        }
    }
}

class XMLReader {
    public String getCataloniaData() {
        File file = new File("catdata.xml");
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

class ProvinceHandler {
    private final StringBuilder capturedProvince = new StringBuilder();
    private boolean capturing = false;

    public void startElement(String qName, Attributes attributes) {
        capturedProvince.append("<").append(qName);
        for (int i = 0; i < attributes.getLength(); i++) {
            capturedProvince.append(" ")
                    .append(attributes.getQName(i))
                    .append("=\"")
                    .append(attributes.getValue(i))
                    .append("\"");
        }
        capturedProvince.append(">");
        capturing = true;
    }

    public void endElement(String qName) {
        capturedProvince.append("</").append(qName).append(">");
    }

    public void characters(char[] ch, int start, int length) {
        if (capturing) {
            capturedProvince.append(new String(ch, start, length));
        }
    }

    public boolean hasCapturedProvince() {
        return capturedProvince.length() > 0;
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

    public void writeCatalonia(TransformerHandler handler, String capturedProvince, String memberships)
            throws SAXException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            // Extract data from captured province
            ProvinceDataExtractor extractor = new ProvinceDataExtractor(capturedProvince);
            String areaValue = extractor.extractArea();
            String populationElements = extractor.extractPopulation();

            // Write Catalonia country
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", "", "car_code", "CDATA", "CAT");
            if (areaValue != null) {
                attrs.addAttribute("", "", "area", "CDATA", areaValue);
            }
            if (memberships != null) {
                attrs.addAttribute("", "", "memberships", "CDATA", memberships);
            }
            handler.startElement("", "", "country", attrs);

            // Parse and write catdata.xml content
            CataloniaContentHandler contentHandler = new CataloniaContentHandler(handler, populationElements);
            parser.parse(new InputSource(new StringReader(xmlReader.getCataloniaData())), contentHandler);

            // Parse and write captured province
            parser.parse(new InputSource(new StringReader(capturedProvince)), new CapturedProvinceHandler(handler));

            handler.endElement("", "", "country");

        } catch (Exception e) {
            throw new SAXException("Error writing Catalonia data", e);
        }
    }
}

class CataloniaContentHandler extends DefaultHandler {
    private final TransformerHandler handler;
    private final String populationElements;
    private boolean skipCountry = true;
    private StringBuilder buffer = new StringBuilder();
    private Map<String, String> elementBuffer = new HashMap<>();
    private List<String> elementOrder = Arrays.asList(
            "name", "localname", "population", "population_growth", "infant_mortality",
            "gdp_total", "gdp_agri", "gdp_ind", "gdp_serv", "inflation", "unemployment",
            "indep_date", "government", "encompassed", "ethnicgroup", "religion",
            "language", "border", "province");
    private int depth = 0;

    public CataloniaContentHandler(TransformerHandler handler, String populationElements) {
        this.handler = handler;
        this.populationElements = populationElements;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        depth++;
        if (skipCountry && qName.equals("country")) {
            skipCountry = false;
            return;
        }

        // Only buffer top-level elements
        if (depth == 2) {
            buffer.setLength(0);
            buffer.append("<").append(qName);
            for (int i = 0; i < attributes.getLength(); i++) {
                buffer.append(" ")
                        .append(attributes.getQName(i))
                        .append("=\"")
                        .append(attributes.getValue(i))
                        .append("\"");
            }
            buffer.append(">");
        } else if (depth > 2) {
            // Directly write nested elements
            handler.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("country") && depth == 1) {
            writeBufferedElements();
        } else if (depth == 2) {
            buffer.append("</").append(qName).append(">");
            String content = buffer.toString();

            if (!elementBuffer.containsKey(qName)) {
                elementBuffer.put(qName, content);
            } else {
                // For multiple occurrences, append to existing content
                elementBuffer.put(qName, elementBuffer.get(qName) + content);
            }

            if (qName.equals("name")) {
                // After name, write population elements, but sort them first
                Map<Integer, String> stringMap = new HashMap<>();
                String[] populationElementsArray = populationElements.split("</population>");
                for (String element : populationElementsArray) {
                    if (!element.trim().isEmpty()) {
                        try {
                            // Find the year attribute value
                            int yearStart = element.indexOf("year=\"") + 6;
                            if (yearStart > 5) { // if year attribute exists
                                int yearEnd = element.indexOf("\"", yearStart);
                                String yearStr = element.substring(yearStart, yearEnd);
                                Integer k = Integer.parseInt(yearStr);
                                stringMap.put(k, element + "</population>");
                            }
                        } catch (Exception e) {
                            // Skip malformed elements
                            continue;
                        }
                    }
                }
                // recompose the population elements in sorted order
                StringBuilder sb = new StringBuilder();
                stringMap.keySet().stream().sorted().forEach(k -> sb.append(stringMap.get(k)));
                elementBuffer.put("population", sb.toString());
            }
        } else if (depth > 2) {
            // Directly write nested element endings
            handler.endElement(uri, localName, qName);
        }
        depth--;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (depth == 2) {
            buffer.append(new String(ch, start, length));
        } else if (depth > 2) {
            handler.characters(ch, start, length);
        }
    }

    private void writeBufferedElements() throws SAXException {
        // Write elements in the correct order according to DTD
        for (String elementName : elementOrder) {
            String content = elementBuffer.get(elementName);
            if (content != null) {
                // Parse and write the buffered content
                try {
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser parser = factory.newSAXParser();
                    parser.parse(new InputSource(new StringReader("<root>" + content + "</root>")),
                            new BufferedContentHandler(handler));
                } catch (Exception e) {
                    throw new SAXException("Error writing buffered content", e);
                }
            }
        }
    }
}

class BufferedContentHandler extends DefaultHandler {
    private final TransformerHandler handler;
    private boolean skipRoot = true;

    public BufferedContentHandler(TransformerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (skipRoot && qName.equals("root")) {
            skipRoot = false;
            return;
        }
        handler.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("root")) {
            return;
        }
        handler.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        handler.characters(ch, start, length);
    }
}

class CapturedProvinceHandler extends DefaultHandler {
    private final TransformerHandler handler;

    public CapturedProvinceHandler(TransformerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("province")) {
            AttributesImpl newAttrs = new AttributesImpl(attributes);
            String country = attributes.getValue("country");
            if (country != null && country.equals("E")) {
                int index = newAttrs.getIndex("country");
                newAttrs.setAttribute(index, "", "country", "country", "CDATA", "CAT");
            }
            handler.startElement(uri, localName, qName, newAttrs);
        } else {
            handler.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        handler.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        handler.characters(ch, start, length);
    }
}

class ProvinceDataExtractor {
    private final String provinceData;

    public ProvinceDataExtractor(String provinceData) {
        this.provinceData = provinceData;
    }

    public String extractArea() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        AreaExtractorHandler handler = new AreaExtractorHandler();
        parser.parse(new InputSource(new StringReader(provinceData)), handler);
        return handler.getArea();
    }

    public String extractPopulation() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        PopulationExtractorHandler handler = new PopulationExtractorHandler();
        parser.parse(new InputSource(new StringReader(provinceData)), handler);
        return handler.getPopulations();
    }
}

class AreaExtractorHandler extends DefaultHandler {
    private boolean inArea = false;
    private String area = null;
    private StringBuilder content = new StringBuilder();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("area")) {
            inArea = true;
            content.setLength(0);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("area")) {
            area = content.toString().trim();
            inArea = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (inArea) {
            content.append(ch, start, length);
        }
    }

    public String getArea() {
        return area;
    }
}

class PopulationExtractorHandler extends DefaultHandler {
    private final StringBuilder populations = new StringBuilder();
    private boolean inPopulation = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("population")) {
            inPopulation = true;
            populations.append("<population");
            for (int i = 0; i < attributes.getLength(); i++) {
                populations.append(" ")
                        .append(attributes.getQName(i))
                        .append("=\"")
                        .append(attributes.getValue(i))
                        .append("\"");
            }
            populations.append(">");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("population")) {
            populations.append("</population>");
            inPopulation = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (inPopulation) {
            populations.append(new String(ch, start, length));
        }
    }

    public String getPopulations() {
        return populations.toString();
    }
}