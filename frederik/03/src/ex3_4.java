import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

// xmllint --dtdvalid ..\..\mondial.dtd updated_mondial.xml
public class ex3_4 {
    public static void main(String[] args) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document mondialDoc = builder.build(new File("../../mondial.xml"));
            Document cataloniaDoc = builder.build(new File("./catdata.xml"));

            Element mondialRoot = mondialDoc.getRootElement();
            Element cataloniaElement = cataloniaDoc.getRootElement();

            Element cataloniaClone = cataloniaElement.clone();
            mondialRoot.addContent(0, cataloniaClone);

            XPathFactory xFactory = XPathFactory.instance();
            XPathExpression<Element> spainExpr = xFactory.compile("//country[@car_code='E']", Filters.element());
            Element spain = spainExpr.evaluateFirst(mondialRoot);

            if (spain != null) {
                List<Element> borders = spain.getChildren("border");
                borders.removeIf(border -> "CAT".equals(border.getAttributeValue("country")));
            }

            XPathExpression<Element> organizationsExpr = xFactory.compile("//organization", Filters.element());
            List<Element> organizations = organizationsExpr.evaluate(mondialRoot);

            for (Element organization : organizations) {
                List<Element> membersList = organization.getChildren("members");
                for (Element members : membersList) {
                    String countryAttr = members.getAttributeValue("country");
                    if (countryAttr != null && countryAttr.contains("E")) {
                        members.setAttribute("country", countryAttr + " CAT");
                    }
                }
            }

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            FileWriter writer = new FileWriter("updated_mondial.xml");
            outputter.output(mondialDoc, writer);
            writer.close();

            System.out.println("Updated mondial.xml successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
