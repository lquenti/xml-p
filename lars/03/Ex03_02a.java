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

class ResultSingleton {
    private static ResultSingleton instance;
    private final List<String> strings;

    private ResultSingleton() {
        strings = new ArrayList<>();
    }

    public static ResultSingleton getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ResultSingleton();
        }
        return instance;
    }

    public void addString(String string) {
        strings.add(string);
    }

    public List<String> getStrings() {
        return Collections.unmodifiableList(strings);
    }

}

class MyContentHandler extends DefaultHandler {
    private boolean inCountry = false;
    private boolean inProvince = false;
    private boolean inCity = false;
    private String currentElement;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String elementName = (qName == null || qName.isEmpty()) ? localName : qName;
        if (Objects.equals(elementName, "country")) {
            inCountry = true;
        } else if (Objects.equals(elementName, "province")) {
            inProvince = true;
        } else if (Objects.equals(elementName, "city")) {
            inCity = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentElement = new String(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        String elementName = (qName == null || qName.isEmpty()) ? localName : qName;
        if (Objects.equals(elementName, "country")) {
            inCountry = false;
        } else if (Objects.equals(elementName, "province")) {
            inProvince = false;
        } else if (Objects.equals(elementName, "city")) {
            inCity = false;
        }
        if (Objects.equals(elementName, "name") && inCountry && !inProvince && !inCity) {
            ResultSingleton.getInstance().addString(currentElement);
        }
    }
}

public class Ex03_02a {
    public static void main(String[] args) throws SAXException, ParserConfigurationException, IOException {
        var handler = new MyContentHandler();
        var factory = SAXParserFactory.newInstance();
        var parser = factory.newSAXParser();
        parser.parse("file:../../mondial.xml", handler);

        List<String> countryNames = ResultSingleton.getInstance().getStrings();

        Document doc = new Document();
        Element html = new Element("html");
        doc.setRootElement(html);
        Element body = new Element("body");
        html.addContent(body);
        Element country;
        for (String countryName : countryNames) {
            country = new Element("p");
            country.setText(countryName);
            body.addContent(country);
        }
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        xmlOutputter.output(doc, new FileOutputStream("Ex03_02a.html"));
    }
}
