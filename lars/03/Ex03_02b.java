import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Output an HTML file that lists the names of all countries in mondial.xml.
 */

class Country_b {
    String name;
    String capital;
    String capitalId;
    String capitalPopulation;

    @Override
    public String toString() {
        return name + "|" + capital + "|" + capitalPopulation;
    }
}

class ResultSingleton_b {
    private static ResultSingleton_b instance;
    private final List<Country_b> items;

    private ResultSingleton_b() {
        items = new ArrayList<>();
    }

    public static ResultSingleton_b getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ResultSingleton_b();
        }
        return instance;
    }

    public void addItem(Country_b item) {
        items.add(item);
    }

    public List<Country_b> getItems() {
        return Collections.unmodifiableList(items);
    }
}


class MyContentHandler_b extends DefaultHandler {
    private boolean inCountry = false;
    private boolean inProvince = false;
    private boolean inCity = false;
    private boolean inCapital = false;
    private Country_b currentCountry;
    private String currentElement;
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        String elementName = (qName == null || qName.isEmpty()) ? localName : qName;
        if (Objects.equals(elementName, "country")) {
            inCountry = true;
            currentCountry = new Country_b();
            currentCountry.capitalId = attributes.getValue("capital");
        } else if (Objects.equals(elementName, "province")) {
            inProvince = true;
        } else if (Objects.equals(elementName, "city")) {
            inCity = true;
            if (Objects.equals(attributes.getValue("id"), currentCountry.capitalId)) {
                inCapital = true;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        String elementName = (qName == null || qName.isEmpty()) ? localName : qName;
        if (Objects.equals(elementName, "country")) {
            inCountry = false;
            ResultSingleton_b.getInstance().addItem(currentCountry);
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
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentElement = new String(ch, start, length);
    }
}

public class Ex03_02b {
    public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException {
        var handler = new MyContentHandler_b();
        var factory = SAXParserFactory.newInstance();
        var parser = factory.newSAXParser();
        parser.parse("file:../../mondial.xml", handler);

        List<Country_b> countries = ResultSingleton_b.getInstance().getItems();
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
        xmlOutputter.output(doc, new FileOutputStream("Ex03_02c.html"));
    }
}
