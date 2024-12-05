import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ex03_02da {
    public static void main(String[] args) throws IOException, XMLStreamException {
        FileInputStream inputStream = new FileInputStream("../../mondial.xml");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader parser = inputFactory.createXMLStreamReader(inputStream);

        boolean inCountry = false;
        boolean inProvince = false;
        boolean inCity = false;
        String currentElement = "";
        List<String> countryNames = new ArrayList<>();

        while (parser.hasNext()) {
            switch (parser.next()) {
                case XMLStreamConstants.START_ELEMENT: {
                    String elementName = parser.getLocalName();
                    if (Objects.equals(elementName, "country")) {
                        inCountry = true;
                    } else if (Objects.equals(elementName, "province")) {
                        inProvince = true;
                    } else if (Objects.equals(elementName, "city")) {
                        inCity = true;
                    }
                    break;
                }
                case XMLStreamConstants.CHARACTERS:
                    currentElement = parser.getText().strip();
                    break;
                case XMLStreamConstants.END_ELEMENT: {
                    String elementName = parser.getLocalName();
                    if (Objects.equals(elementName, "country")) {
                        inCountry = false;
                    } else if (Objects.equals(elementName, "province")) {
                        inProvince = false;
                    } else if (Objects.equals(elementName, "city")) {
                        inCity = false;
                    }
                    if (Objects.equals(elementName, "name") && inCountry && !inProvince && !inCity) {
                        countryNames.add(currentElement);
                    }
                }
            }
        }
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
        xmlOutputter.output(doc, new FileOutputStream("Ex03_02da.html"));
    }
}
