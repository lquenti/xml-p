package org.example;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class E31 {
    /**
     * Parse mondial.xml into a DOM instance and implement the following query based on the DOM
     * operations (do not apply XPath in DOM):
     *
     * @return
     * @throws IOException
     * @throws JDOMException
     */
    public static Element readMondial() throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(App.MONDIAL_XML_PATH);
        return document.getRootElement();
    }


    public static void run() throws IOException, JDOMException {
        /*
         * For all organisations that have their headquarter in the capital of a member country, output the
         * name of the organisation and the name of the headquarter (to System.out)
         */
        Element root = E31.readMondial();
        Map<String, Element> carCodeCountriesMap = root.getChildren("country").stream().collect(
                Collectors.toMap(e -> e.getAttributeValue("car_code"), Function.identity())
        );
        //        For all organisations that have their headquarter
        List<Element> organizations = root.getChildren("organization");
        for (Element organization : organizations) {
            Attribute headquarters = organization.getAttribute("headq");
            if (headquarters == null) {
                continue;
            }
            //headquarter in the capital of a member country,
            Set<String> member_capitals = organization.getChildren("members").stream().flatMap(
                            members -> Arrays
                                    .stream(members.getAttribute("country")
                                            .getValue()
                                            .split(" "))
                    ).map(country_code -> carCodeCountriesMap
                            .get(country_code)
                            .getAttribute("capital")
                            .getValue())
                    .collect(Collectors.toSet());

            if (member_capitals.contains(headquarters.getValue())) {
                System.out.println(organization
                        .getChild("name")
                        .getText() + " [" + headquarters
                        .getValue() + "]");
            }
        }
    }
}
