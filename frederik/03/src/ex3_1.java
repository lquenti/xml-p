import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ex3_1 {
    public static void main(String[] args) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new File("../../mondial.xml"));

            Element rootElement = document.getRootElement();

            List<Element> organizations = rootElement.getChildren("organization");

            for (Element organization : organizations) {
                String headqId = organization.getAttributeValue("headq");

                if (headqId == null) continue;

                Element headqElement = findCityById(rootElement, headqId);

                if (headqElement == null) continue;

                String countryId = headqElement.getAttributeValue("country");
                Element countryElement = findCountryById(rootElement, countryId);

                if (countryElement != null) {
                    String capitalId = countryElement.getAttributeValue("capital");
                    if (capitalId != null && capitalId.equals(headqId)) {
                        String orgName = organization.getChildText("name");
                        String headqName = headqElement.getChildText("name");
                        System.out.println("Organization: " + orgName + ", Headquarters: " + headqName);
                    }
                }
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    private static Element findCityById(Element root, String id) {
        for (Element country : root.getChildren("country")) {
            for (Element province : country.getChildren("province")) {
                List<Element> cities = province.getChildren("city");
                for (Element city : cities) {
                    if (id.equals(city.getAttributeValue("id"))) {
                        return city;
                    }
                }
            }
        }
        return null;
    }

    private static Element findCountryById(Element root, String id) {
        List<Element> countries = root.getChildren("country");
        for (Element country : countries) {
            if (id.equals(country.getAttributeValue("car_code"))) {
                return country;
            }
        }
        return null;
    }
}
