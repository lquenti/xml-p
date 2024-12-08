import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ex3_3_4 {
    public static void main(String[] args) {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        try (PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream)) {

            // Thread for producing XML results
            Thread producer = new Thread(() -> {
                try {
                    produceResults(pipedOutputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // Thread for consuming and filtering <city> elements
            Thread consumer = new Thread(() -> {
                try {
                    filterCityElements(pipedInputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            producer.start();
            consumer.start();

            producer.join();
            consumer.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void produceResults(OutputStream outputStream) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        FileInputStream inputStream = new FileInputStream("mondial.xml");
        XMLEventReader eventReader = factory.createXMLEventReader(inputStream);

        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(outputStream, "UTF-8");

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        eventWriter.add(eventFactory.createStartDocument());
        eventWriter.add(eventFactory.createStartElement("", "", "results"));

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
                    if (currentCityName == null) {
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

                                eventWriter.add(eventFactory.createStartElement("", "", "result"));

                                eventWriter.add(eventFactory.createStartElement("", "", "organization"));
                                eventWriter.add(eventFactory.createAttribute("name", organizationName));
                                eventWriter.add(eventFactory.createEndElement("", "", "organization"));

                                eventWriter.add(eventFactory.createStartElement("", "", "city"));
                                eventWriter.add(eventFactory.createAttribute("name", capitalName));
                                eventWriter.add(eventFactory.createEndElement("", "", "city"));

                                eventWriter.add(eventFactory.createEndElement("", "", "result"));

                                System.out.println("Produced: Organization=" + organizationName + ", City=" + capitalName);
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

        eventWriter.add(eventFactory.createEndElement("", "", "results"));
        eventWriter.add(eventFactory.createEndDocument());

        eventWriter.close();
        System.out.println("Producer finished.");
    }

    // Method to consume and filter <city> elements
    public static void filterCityElements(InputStream inputStream) throws Exception {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = factory.createXMLEventReader(inputStream);

        while (true) {
            try {
                if (!eventReader.hasNext()) break;
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    if ("city".equals(startElement.getName().getLocalPart())) {
                        String cityName = startElement.getAttributeByName(new QName("name")).getValue();
                        System.out.println("Filtered: City=" + cityName);
                    }
                }
            } catch (XMLStreamException e) {
                System.out.println("Consumer: End of stream or malformed XML.");
                break;
            }
        }

        eventReader.close();
        System.out.println("Consumer finished.");
    }

}
