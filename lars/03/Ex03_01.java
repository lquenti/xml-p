import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Parse mondial.xml into a DOM instance and implement the following query based on the DOM
 * operations (do not apply XPath in DOM):
 * For all organisations that have their headquarter in the capital of a member country, output the
 * name of the organisation and the name of the headquarter (to System.out).
 */

public class Ex03_01 {
    private static Element load_mondial() throws IOException, JDOMException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new File("../../mondial.xml"));
        return doc.getRootElement();
    }

    /* We dont do error handling */
    public static void main(String[] args) throws Exception {
        var mondial = load_mondial();
        Map<String, Element> countries = mondial.getChildren("country").stream().collect(
                Collectors.toMap(e -> e.getAttributeValue("car_code"), Function.identity())
        );

        for (var org : mondial.getChildren("organization")) {
            var headq = org.getAttribute("headq");
            if (headq == null) {
                continue;
            }
            /* We want to look at all type of members (i.e. regional member, nonregional member, member...) */
            var member_capitals = org.getChildren("members").stream().flatMap(
                    members -> Arrays.stream(members.getAttribute("country").getValue().split(" "))
                    ).map(country_code -> countries.get(country_code).getAttribute("capital").getValue())
                    .collect(Collectors.toSet());
            if (member_capitals.contains(headq.getValue())) {
                System.out.println(org.getChild("name").getText() + " - " + headq.getValue());
            }
        }
    }
}

