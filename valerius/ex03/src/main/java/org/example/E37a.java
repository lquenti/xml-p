package org.example;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class E37a {

    private static final String CATALONIA_XML = "<country car_code=\"CAT\">\n" +
            "  <name>Catalonia</name>\n" +
            "  <population_growth>0.8</population_growth>\n" +
            "  <infant_mortality>2.5</infant_mortality>\n" +
            "  <gdp_total>204189</gdp_total>\n" +
            "  <gdp_agri>3</gdp_agri>\n" +
            "  <gdp_ind>37</gdp_ind>\n" +
            "  <gdp_serv>60</gdp_serv>\n" +
            "  <inflation>1.5</inflation>\n" +
            "  <unemployment>12.6</unemployment>\n" +
            "  <indep_date from=\"E\">2018-04-01</indep_date>\n" +
            "  <government>Dictatorship</government>\n" +
            "  <encompassed continent=\"europe\" percentage=\"100\"/>\n" +
            "  <ethnicgroup percentage=\"100\">Mediterranean Nordic</ethnicgroup>\n" +
            "  <religion percentage=\"52.4\">Roman Catholic</religion>\n" +
            "  <religion percentage=\"2.5\">Protestant</religion>\n" +
            "  <religion percentage=\"7.3\">Muslim</religion>\n" +
            "  <religion percentage=\"1.3\">Buddhist</religion>\n" +
            "  <religion percentage=\"1.2\">Christian Orthodox</religion>\n" +
            "  <language percentage=\"52\">Spanish</language>\n" +
            "  <language percentage=\"41.5\">Catalan</language>\n" +
            "  <language percentage=\"0.1\">Occitan</language>\n" +
            "  <border country=\"AND\" length=\"65\"/>\n" +
            "  <border country=\"F\" length=\"300\"/>\n" +
            "  <border country=\"E\" length=\"320\"/>\n" +
            "</country>";

    private static final String PROVINCE_ID = "prov-Spain-11";
    private static final Map<String, String> replaceMap = new HashMap<>();

    static {
        replaceMap.put("prov-Spain-11", "prov-Catalonia-1");
        replaceMap.put("cty-Spain-Barcelona", "cty-Catalonia-Barcelona");
    }

    public static void main(String[] args) {
        try {
            E37a ex = new E37a();
            ex.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
        // Parse mondial.xml using StAX and build an in-memory representation
        InputStream inputStream = new FileInputStream("mondial.xml");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(inputStream);

        List<Map<String, Object>> countries = new ArrayList<>();
        Map<String, Object> currentElementMap = null;
        Stack<Map<String, Object>> elementStack = new Stack<>();

        Map<String, Object> mondial = new HashMap<>();
        mondial.put("name", "mondial");
        mondial.put("elements", new ArrayList<Map<String, Object>>());

        while (reader.hasNext()) {
            int eventType = reader.next();
            switch (eventType) {
                case XMLStreamConstants.START_ELEMENT:
                    String currentElementName = reader.getLocalName();
                    Map<String, Object> elementData = new HashMap<>();
                    elementData.put("name", currentElementName);
                    for (int i = 0; i < reader.getAttributeCount(); i++) {
                        elementData.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                    }
                    elementData.put("elements", new ArrayList<Map<String, Object>>());

                    if (elementStack.isEmpty()) {
                        // Root element (mondial)
                        mondial = elementData;
                    } else {
                        Map<String, Object> parent = elementStack.peek();
                        List<Map<String, Object>> elements = (List<Map<String, Object>>) parent.get("elements");
                        elements.add(elementData);
                    }

                    if ("country".equals(currentElementName)) {
                        countries.add(elementData);
                    }

                    elementStack.push(elementData);
                    break;

                case XMLStreamConstants.CHARACTERS:
                    String text = reader.getText().trim();
                    if (!text.isEmpty()) {
                        Map<String, Object> currentElementData = elementStack.peek();
                        currentElementData.put("text", text);
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    elementStack.pop();
                    break;
            }
        }
        reader.close();
        inputStream.close();

        // Locate Spain and Catalonia data
        Map<String, Object> spain = null;
        for (Map<String, Object> country : countries) {
            if ("E".equals(country.get("car_code"))) {
                spain = country;
                break;
            }
        }

        if (spain == null) {
            throw new RuntimeException("Spain not found in mondial.xml");
        }

        // Parse Catalonia XML
        InputStream cataloniaStream = new ByteArrayInputStream(CATALONIA_XML.getBytes());
        XMLStreamReader cataloniaReader = inputFactory.createXMLStreamReader(cataloniaStream);

        Map<String, Object> catalonia = null;
        Stack<Map<String, Object>> cataloniaStack = new Stack<>();

        while (cataloniaReader.hasNext()) {
            int eventType = cataloniaReader.next();
            switch (eventType) {
                case XMLStreamConstants.START_ELEMENT:
                    String cataloniaCurrentElement = cataloniaReader.getLocalName();
                    Map<String, Object> elementData = new HashMap<>();
                    elementData.put("name", cataloniaCurrentElement);
                    for (int i = 0; i < cataloniaReader.getAttributeCount(); i++) {
                        elementData.put(cataloniaReader.getAttributeLocalName(i), cataloniaReader.getAttributeValue(i));
                    }
                    elementData.put("elements", new ArrayList<Map<String, Object>>());

                    if (cataloniaStack.isEmpty()) {
                        // This is the root 'country' element
                        catalonia = elementData;
                    } else {
                        Map<String, Object> parent = cataloniaStack.peek();
                        List<Map<String, Object>> elements = (List<Map<String, Object>>) parent.get("elements");
                        elements.add(elementData);
                    }

                    cataloniaStack.push(elementData);
                    break;

                case XMLStreamConstants.CHARACTERS:
                    String text = cataloniaReader.getText().trim();
                    if (!text.isEmpty()) {
                        Map<String, Object> currentCataloniaElement = cataloniaStack.peek();
                        currentCataloniaElement.put("text", text);
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    cataloniaStack.pop();
                    break;
            }
        }
        cataloniaReader.close();
        cataloniaStream.close();

        // Modify data
        modifyData(mondial, spain, catalonia, countries);

        // Write the modified data back to an XML file
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(new FileOutputStream("output_3_7a.xml"), "UTF-8");

        // Write XML declaration and DOCTYPE
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeDTD("\n<!DOCTYPE mondial SYSTEM \"mondial.dtd\">");

        // Write the mondial element
        writeElement(writer, mondial);

        writer.writeEndDocument();
        writer.flush();
        writer.close();

        // Add this at the end of the run() method, after the writer.close();
        String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("output_3_7a.xml")));

        // Perform the replacements, to fix the IDs
        content = content.replace("cty-Spain-Barcelona", "cty-Catalonia-Barcelona")
                        .replace("prov-Spain-11", "prov-Catalonia-1")
                        .replace("cty-Spain-61", "cty-Catalonia-61");

        // Write the modified content back to the file
        java.nio.file.Files.write(java.nio.file.Paths.get("output_3_7a.xml"), content.getBytes());
    }

    private void modifyData(Map<String, Object> mondial, Map<String, Object> spain, Map<String, Object> catalonia, List<Map<String, Object>> countries) {
        // Remove Catalonia province from Spain
        List<Map<String, Object>> spainElements = (List<Map<String, Object>>) spain.get("elements");
        Map<String, Object> cataloniaProvince = null;
        Iterator<Map<String, Object>> iterator = spainElements.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> element = iterator.next();
            if ("province".equals(element.get("name")) && PROVINCE_ID.equals(element.get("id"))) {
                cataloniaProvince = element;
                iterator.remove();
                break;
            }
        }
        if (cataloniaProvince == null) {
            throw new RuntimeException("Catalonia province not found in Spain");
        }

        // Modify province attributes
        cataloniaProvince.put("id", "prov-Catalonia-1");
        cataloniaProvince.put("country", "CAT");
        cataloniaProvince.put("capital", "cty-Catalonia-Barcelona");

        // Modify cities within the province
        List<Map<String, Object>> provinceElements = (List<Map<String, Object>>) cataloniaProvince.get("elements");
        for (Map<String, Object> city : provinceElements) {
            if ("city".equals(city.get("name"))) {
                city.put("id", city.get("id").toString().replace("Spain", "Catalonia"));
                city.put("country", "CAT");
                city.put("province", "prov-Catalonia-1");
            }
        }

        // Adjust areas and populations
        adjustAreaAndPopulation(spain, catalonia, cataloniaProvince);

        // Add province to Catalonia
        List<Map<String, Object>> cataloniaElements = (List<Map<String, Object>>) catalonia.get("elements");
        cataloniaElements.add(cataloniaProvince);

        // Add Catalonia to the root mondial elements
        List<Map<String, Object>> mondialElements = (List<Map<String, Object>>) mondial.get("elements");
        mondialElements.add(0, catalonia);

        // TODO: Modify borders, rivers, mountains, seas, etc.
        //       Implement logic similar to the E34 code
    }

    private void adjustAreaAndPopulation(Map<String, Object> spain, Map<String, Object> catalonia, Map<String, Object> cataloniaProvince) {
        // Adjust areas
        String provinceArea = null;
        List<Map<String, Object>> provinceElements = (List<Map<String, Object>>) cataloniaProvince.get("elements");
        for (Map<String, Object> element : provinceElements) {
            if ("area".equals(element.get("name"))) {
                provinceArea = element.get("text").toString();
                break;
            }
        }

        if (provinceArea != null) {
            catalonia.put("area", provinceArea); // Set area attribute in catalonia
            String spainArea = (String) spain.get("area");
            if (spainArea != null) {
                double spainAreaValue = Double.parseDouble(spainArea);
                double provinceAreaValue = Double.parseDouble(provinceArea);
                spain.put("area", String.valueOf(spainAreaValue - provinceAreaValue));
            }
        }

        // Adjust populations
        String provincePopulation = null;
        for (Map<String, Object> element : provinceElements) {
            if ("population".equals(element.get("name"))) {
                provincePopulation = element.get("text").toString();
            }
        }

        if (provincePopulation != null) {
            List<Map<String, Object>> cataloniaElements = (List<Map<String, Object>>) catalonia.get("elements");
            Map<String, Object> populationElement = new HashMap<>();
            populationElement.put("name", "population");
            populationElement.put("text", provincePopulation);
            populationElement.put("elements", new ArrayList<Map<String, Object>>());
            cataloniaElements.add(populationElement);

            List<Map<String, Object>> spainElements = (List<Map<String, Object>>) spain.get("elements");
            for (Map<String, Object> element : spainElements) {
                if ("population".equals(element.get("name"))) {
                    String spainPopulation = element.get("text").toString();
                    int spainPopValue = Integer.parseInt(spainPopulation);
                    int provincePopValue = Integer.parseInt(provincePopulation);
                    element.put("text", String.valueOf(spainPopValue - provincePopValue));
                    break;
                }
            }
        }
    }

    private void writeElement(XMLStreamWriter writer, Map<String, Object> element) throws XMLStreamException {
        String name = (String) element.get("name");
        writer.writeStartElement(name);

        // Write attributes
        for (Map.Entry<String, Object> attr : element.entrySet()) {
            if (!"name".equals(attr.getKey()) && !"text".equals(attr.getKey()) && !"elements".equals(attr.getKey()) && !"parent".equals(attr.getKey())) {
                writer.writeAttribute(attr.getKey(), attr.getValue().toString());
            }
        }

        // Write text content if any
        if (element.containsKey("text")) {
            writer.writeCharacters(element.get("text").toString());
        }

        // Write child elements in order according to DTD
        if (element.containsKey("elements")) {
            List<Map<String, Object>> childElements = (List<Map<String, Object>>) element.get("elements");

            // If the current element is 'country', sort child elements according to DTD
            if ("country".equals(name)) {
                List<Map<String, Object>> sortedElements = sortCountryElements(childElements);
                for (Map<String, Object> child : sortedElements) {
                    writeElement(writer, child);
                }
            } else {
                for (Map<String, Object> child : childElements) {
                    writeElement(writer, child);
                }
            }
        }

        writer.writeEndElement();
    }

    private List<Map<String, Object>> sortCountryElements(List<Map<String, Object>> elements) {
        // Define the order according to the DTD
        String[] countryElementOrder = {
            "name",
            "localname",
            "population",
            "population_growth",
            "infant_mortality",
            "gdp_total",
            "gdp_agri",
            "gdp_ind",
            "gdp_serv",
            "inflation",
            "unemployment",
            "indep_date",
            "dependent",
            "government",
            "encompassed",
            "ethnicgroup",
            "religion",
            "language",
            "border",
            "province",
            "city"
        };

        Map<String, Integer> orderMap = new HashMap<>();
        int index = 0;
        for (String elemName : countryElementOrder) {
            orderMap.put(elemName, index++);
        }

        // Sort the elements based on the defined order
        elements.sort((e1, e2) -> {
            String name1 = (String) e1.get("name");
            String name2 = (String) e2.get("name");
            int order1 = orderMap.getOrDefault(name1, Integer.MAX_VALUE);
            int order2 = orderMap.getOrDefault(name2, Integer.MAX_VALUE);
            if (order1 != order2) {
                return Integer.compare(order1, order2);
            } else {
                // If same order, maintain the sequence
                return 0;
            }
        });

        return elements;
    }
}