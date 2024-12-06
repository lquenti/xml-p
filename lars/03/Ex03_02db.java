import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ex03_02db {
    public static void main(String[] args) throws IOException, XMLStreamException {
        FileInputStream inputStream = new FileInputStream("../../mondial.xml");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader parser = inputFactory.createXMLStreamReader(inputStream);

        List<Country_b> countries = new ArrayList<>();

        boolean inCountry = false;
        boolean inProvince = false;
        boolean inCity = false;
        boolean inCapital = false;
        Country_b currentCountry = new Country_b();
        String currentElement = "";

        while (parser.hasNext()) {
            switch (parser.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    String elementName = parser.getLocalName();
                    if (Objects.equals(elementName, "country")) {
                        inCountry = true;
                        currentCountry = new Country_b();
                        currentCountry.capitalId = parser.getAttributeValue(null, "capital");
                    } else if (Objects.equals(elementName, "province")) {
                        inProvince = true;
                    } else if (Objects.equals(elementName, "city")) {
                        inCity = true;
                        if (Objects.equals(parser.getAttributeValue(null, "id"), currentCountry.capitalId)) {
                            inCapital = true;
                        }
                    }
                    break;
                }
                case XMLStreamConstants.CHARACTERS: {
                    currentElement = parser.getText().strip();
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    String elementName = parser.getLocalName();
                    if (Objects.equals(elementName, "country")) {
                        inCountry = false;
                        countries.add(currentCountry);
                    } else if (Objects.equals(elementName, "province")) {
                        inProvince = false;
                    } else if (Objects.equals(elementName, "city")) {
                        inCity = false;
                        inCapital = false;
                    } else if (Objects.equals(elementName, "population")) {
                        if (inCapital) {
                            currentCountry.capitalPopulation = currentElement;
                        }
                    } else if (Objects.equals(elementName, "name")) {
                        if (inCapital) {
                            currentCountry.capital = currentElement;
                        } else if (inCountry && !inProvince && !inCity) {
                            currentCountry.name = currentElement;
                        }
                    }
                    break;
                }
            }
        }

        System.out.println("country|capital|capital population");
        countries.forEach(System.out::println);

        Document doc = new Document();
        Element html = new Element("html");
        doc.setRootElement(html);
        Element body = new Element("body");
        html.addContent(body);
        Element table = new Element("table");
        body.addContent(table);
        Element tr = new Element("tr");
        table.addContent(tr);
        Element th1 = new Element("th");
        th1.setText("country");
        tr.addContent(th1);
        Element th2 = new Element("th");
        th2.setText("capital");
        tr.addContent(th2);
        Element th3 = new Element("th");
        th3.setText("capital population");
        tr.addContent(th3);

        for (var c : countries) {
            Element row = new Element("tr");
            Element country = new Element("td");
            country.setText(c.name);
            row.addContent(country);
            Element capital = new Element("td");
            capital.setText(c.capital);
            row.addContent(capital);
            Element population = new Element("td");
            population.setText(c.capitalPopulation);
            row.addContent(population);
            table.addContent(row);
        }

        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        xmlOutputter.output(doc, new FileOutputStream("Ex03_02dc.html"));
    }
}
