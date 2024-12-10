import org.jdom2.Attribute;
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
import java.util.*;

// xmllint --dtdvalid ..\..\mondial.dtd updated_mondial.xml
public class ex3_4 {
    public static void main(String[] args) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document mondialDoc = builder.build(new File("../../mondial.xml"));
            Document cataloniaDoc = builder.build(new File("./catdata.xml"));

            // Get root elements
            Element mondialRoot = mondialDoc.getRootElement();
            Element cataloniaElement = cataloniaDoc.getRootElement();

            // 1. Add Catalonia as a new country at the top
            Element cataloniaClone = cataloniaElement.clone();
            mondialRoot.addContent(0, cataloniaClone); // Add at the top

            // 2. Transfer data and cities from Spain's province to Catalonia
            XPathFactory xFactory = XPathFactory.instance();
            XPathExpression<Element> spainExpr = xFactory.compile("//country[@car_code='E']", Filters.element());
            Element spain = spainExpr.evaluateFirst(mondialRoot);

            if (spain != null) {
                // Find the province with ID prov-Spain-11
                XPathExpression<Element> provinceExpr = xFactory.compile(".//province[@id='prov-Spain-11']", Filters.element());
                Element province = provinceExpr.evaluateFirst(spain);

                if (province != null) {
                    // Transfer the province to Catalonia
                    spain.removeContent(province); // Remove from Spain
                    Element p = province.clone();
                    p.setAttribute("country", "CAT");
                    cataloniaClone.addContent(p); // Add to Catalonia
                }
            }

            // 3. Update references for `prov-Spain-11` to be associated with `CAT`
            XPathExpression<Attribute> attrExpr = xFactory.compile("//@*[contains(., 'prov-Spain-11')]", Filters.attribute());
            List<Attribute> attributes = attrExpr.evaluate(mondialRoot);
            for (Attribute attr : attributes) {
                String value = attr.getValue();
                // Replace references from "country=E" to "country=CAT"
                value = value.replace("country=\"E\"", "country=\"CAT\"");
                attr.setValue(value);
            }

            XPathExpression<Element> textExpr = xFactory.compile("//*[contains(text(), 'prov-Spain-11')]", Filters.element());
            List<Element> elements = textExpr.evaluate(mondialRoot);
            for (Element element : elements) {
                String text = element.getText();
                // Update any additional text references as needed
                element.setText(text.replace("country=\"E\"", "country=\"CAT\""));
            }

            // 4. Handle located elements for prov-Spain-11
            XPathExpression<Element> locatedExpr = xFactory.compile("//located[contains(@province, 'prov-Spain-11')]", Filters.element());
            List<Element> locatedElements = locatedExpr.evaluate(mondialRoot);

            for (Element located : locatedElements) {
                // Get parent element before making changes
                Element parent = located.getParentElement();
                if (parent == null) continue; // Ignore if no parent

                // Add a new located entry for Catalonia with prov-Spain-11
                // This happens first to ensure it exists before modifying/removing Spain's entry
                List<Element> siblings = parent.getChildren("located");

                Element newLocated = new Element("located");
                newLocated.setAttribute("country", "CAT");
                newLocated.setAttribute("province", "prov-Spain-11");
                parent.addContent(newLocated); // Add new <located> in the correct position

                // Add "CAT" to the country attribute of the parent
                String parentCountries = parent.getAttributeValue("country");
                if (parentCountries != null && !parentCountries.contains("CAT")) {
                    parentCountries = "CAT " + parentCountries; // Prepend CAT for consistency
                    parent.setAttribute("country", parentCountries.trim());
                }

                // Remove prov-Spain-11 from Spain's entry
                String provinces = located.getAttributeValue("province");
                String countries = located.getAttributeValue("country");

                boolean shouldRemoveLocated = false;
                if (countries != null && countries.contains("E")) {
                    provinces = provinces.replace("prov-Spain-11", "").trim();
                    provinces = provinces.replaceAll("\\s+", " "); // Normalize spaces

                    if (provinces.isEmpty()) {
                        // Mark the <located> for removal if no provinces remain
                        shouldRemoveLocated = true;
                    } else {
                        located.setAttribute("province", provinces);
                    }
                }

                // Remove <located> for Spain if no provinces remain
                if (shouldRemoveLocated) {
                    located.detach();

                    // Check if Spain should be removed from the country attribute
                    boolean hasOtherLocatedForSpain = parent.getChildren("located").stream()
                            .anyMatch(l -> "E".equals(l.getAttributeValue("country")));
                    if (!hasOtherLocatedForSpain) {
                        parentCountries = parent.getAttributeValue("country");
                        if (parentCountries != null) {
                            parentCountries = parentCountries.replace("E", "").trim();
                            parentCountries = parentCountries.replaceAll("\\s+", " "); // Normalize spaces
                            parent.setAttribute("country", parentCountries);
                        }
                    }
                }
            }

            // 5. Add Catalonia to organizations where Spain is a member
            XPathExpression<Element> organizationsExpr = xFactory.compile("//organization", Filters.element());
            List<Element> organizations = organizationsExpr.evaluate(mondialRoot);

            for (Element organization : organizations) {
                List<Element> membersList = organization.getChildren("members");
                for (Element members : membersList) {
                    String countryAttr = members.getAttributeValue("country");
                    if (countryAttr != null && countryAttr.contains("E")) {
                        // Add CAT if not already present
                        if (!countryAttr.contains("CAT")) {
                            members.setAttribute("country", countryAttr + " CAT");
                        }
                    }
                }
            }

            // 7. Adjust Spain's attributes by subtracting Catalonia's data
            double spainPopulation = 0;
            int year = 0;
            for(Element pops: spain.getChildren("population")){
                if(Integer.parseInt(pops.getAttribute("year").getValue()) > year){
                    year = Integer.parseInt(pops.getAttribute("year").getValue());
                    spainPopulation = Double.parseDouble(pops.getText());
                }
            }

            // Get Spain's attributes
            double spainPopulationGrowth = Double.parseDouble(spain.getChildText("population_growth"));
            double spainInfantMortality = Double.parseDouble(spain.getChildText("infant_mortality"));
            double spainGdpTotal = Double.parseDouble(spain.getChildText("gdp_total"));
            double spainGdpAgri = Double.parseDouble(spain.getChildText("gdp_agri"));
            double spainGdpInd = Double.parseDouble(spain.getChildText("gdp_ind"));
            double spainGdpServ = Double.parseDouble(spain.getChildText("gdp_serv"));
            double spainInflation = Double.parseDouble(spain.getChildText("inflation"));
            double spainUnemployment = Double.parseDouble(spain.getChildText("unemployment"));

            // Get Catalonia's attributes from the cloned elemenz
            double catPopulation = 0;
            year = 0;
            for(Element pops: cataloniaClone.getChild("province").getChildren("population")){
                if(Integer.parseInt(pops.getAttribute("year").getValue()) > year){
                    year = Integer.parseInt(pops.getAttribute("year").getValue());
                    catPopulation = Double.parseDouble(pops.getText());
                }
            }
            double catPopulationGrowth = Double.parseDouble(cataloniaClone.getChildText("population_growth"));
            double catInfantMortality = Double.parseDouble(cataloniaClone.getChildText("infant_mortality"));
            double catGdpTotal = Double.parseDouble(cataloniaClone.getChildText("gdp_total"));
            double catGdpAgri = Double.parseDouble(cataloniaClone.getChildText("gdp_agri"));
            double catGdpInd = Double.parseDouble(cataloniaClone.getChildText("gdp_ind"));
            double catGdpServ = Double.parseDouble(cataloniaClone.getChildText("gdp_serv"));
            double catInflation = Double.parseDouble(cataloniaClone.getChildText("inflation"));
            double catUnemployment = Double.parseDouble(cataloniaClone.getChildText("unemployment"));

            // Calculate new values
            double newPopulation = spainPopulation - catPopulation;
            double newPopulationGrowth = ((spainPopulation * spainPopulationGrowth) - (catPopulation * catPopulationGrowth)) / newPopulation;
            double newInfantMortality = ((spainPopulation * spainInfantMortality) - (catPopulation * catInfantMortality)) / newPopulation;
            double newGdpTotal = spainGdpTotal - catGdpTotal;
            double newGdpAgri = ((spainGdpTotal * spainGdpAgri / 100) - (catGdpTotal * catGdpAgri / 100)) / newGdpTotal * 100;
            double newGdpInd = ((spainGdpTotal * spainGdpInd / 100) - (catGdpTotal * catGdpInd / 100)) / newGdpTotal * 100;
            double newGdpServ = ((spainGdpTotal * spainGdpServ / 100) - (catGdpTotal * catGdpServ / 100)) / newGdpTotal * 100;
            double newInflation = ((spainGdpTotal * spainInflation) - (catGdpTotal * catInflation)) / newGdpTotal;
            double newUnemployment = ((spainPopulation * spainUnemployment) - (catPopulation * catUnemployment)) / newPopulation;

            // Update Spain's attributes
            spain.getChild("population").setText(String.format(Locale.ENGLISH,"%.0f", newPopulation));
            spain.getChild("population_growth").setText(String.format(Locale.ENGLISH,"%.2f", newPopulationGrowth));
            spain.getChild("infant_mortality").setText(String.format(Locale.ENGLISH,"%.2f", newInfantMortality));
            spain.getChild("gdp_total").setText(String.format(Locale.ENGLISH,"%.0f", newGdpTotal));
            spain.getChild("gdp_agri").setText(String.format(Locale.ENGLISH,"%.1f", newGdpAgri));
            spain.getChild("gdp_ind").setText(String.format(Locale.ENGLISH,"%.1f", newGdpInd));
            spain.getChild("gdp_serv").setText(String.format(Locale.ENGLISH,"%.1f", newGdpServ));
            spain.getChild("inflation").setText(String.format(Locale.ENGLISH,"%.1f", newInflation));
            spain.getChild("unemployment").setText(String.format(Locale.ENGLISH,"%.1f", newUnemployment));

            spain.setAttribute("area",
                    String.valueOf(Integer.parseInt(spain.getAttributeValue("area")) -
                            Integer.parseInt(cataloniaClone.getChild("province").getChildText("area"))));
            // Handle ethnic groups
            // ?????

            // Update religion percentages for Spain
            List<Element> spainReligions = spain.getChildren("religion");
            List<Element> cataloniaReligions = cataloniaClone.getChildren("religion");
            for (Element spainReligion : spainReligions) {
                String religionName = spainReligion.getText(); // Get religion name
                double spainRelPercentage = Double.parseDouble(spainReligion.getAttributeValue("percentage"));
                double catRelPercentage = 0.0;
                // Find the corresponding religion in Catalonia, if it exists
                for (Element catReligion : cataloniaReligions) {
                    if (catReligion.getText().contains(religionName)) {
                        catRelPercentage = Double.parseDouble(catReligion.getAttributeValue("percentage"));
                        break;
                    }
                }
                // Calculate the new percentage for the religion
                double newRelPercentage = ((spainPopulation *  spainRelPercentage/ 100) - (catPopulation * catRelPercentage/ 100)) / (newPopulation) * 100;
                if (newRelPercentage > 0) {
                    spainReligion.setAttribute("percentage", String.format(Locale.ENGLISH,"%.1f", newRelPercentage));
                } else {
                    spain.getChildren().remove(spainReligion); // Remove if percentage drops to zero or below
                }
            }

            // Update languages percentages for Spain
            List<Element> spainLanguages = spain.getChildren("language");
            List<Element> cataloniaLanguages = cataloniaClone.getChildren("language");
            for (Element spainLanguage : spainLanguages) {
                String languageName = spainLanguage.getText(); // Get religion name
                double spainLangPercentage = Double.parseDouble(spainLanguage.getAttributeValue("percentage"));
                double catLangPercentage = 0.0;
                // Find the corresponding religion in Catalonia, if it exists
                for (Element cataloniaLanguage : cataloniaLanguages) {
                    if (cataloniaLanguage.getText().contains(languageName)) {
                        catLangPercentage = Double.parseDouble(cataloniaLanguage.getAttributeValue("percentage"));
                        break;
                    }
                }
                // Calculate the new percentage for the languages
                double newLangPercentage = ((spainPopulation *  spainLangPercentage/ 100) - (catPopulation * catLangPercentage/ 100)) / (newPopulation) * 100;
                if (newLangPercentage > 0) {
                    spainLanguage.setAttribute("percentage", String.format(Locale.ENGLISH,"%.1f", newLangPercentage));
                } else {
                    spain.getChildren().remove(spainLanguage); // Remove if percentage drops to zero or below
                }
            }

            // Handle borders
            List<Element> spainBorders = spain.getChildren("border");
            List<Element> catBorders = cataloniaClone.getChildren("border");
            for (Element catBorder : catBorders) {
                String catNeighbor = catBorder.getAttributeValue("country");
                double catBorderLength = Double.parseDouble(catBorder.getAttributeValue("length"));
                if(catNeighbor.equals("E")) {
                    Element e = new Element("border");
                    e.setAttribute("country", "CAT");
                    e.setAttribute("length", String.valueOf(catBorderLength));
                    spain.addContent(e);
                }

                XPathExpression<Element> countryExpr = xFactory.compile(String.format("//country[@car_code='%s']", catNeighbor), Filters.element());
                Element country = countryExpr.evaluateFirst(mondialRoot);
                for (Element spainBorder : spainBorders) {
                    if (spainBorder.getAttributeValue("country").equals(catNeighbor)) {
                        double spainBorderLength = Double.parseDouble(spainBorder.getAttributeValue("length"));
                        spainBorderLength -= catBorderLength;

                        if (spainBorderLength <= 0) {
                            spain.removeContent(spainBorder);
                        } else {
                            spainBorder.setAttribute("length", String.format(Locale.ENGLISH,"%.0f", spainBorderLength));
                        }
                        break;
                    }
                }
                List<Element> countryBorders = country.getChildren("border");
                for (Element countryBorder : countryBorders) {
                    if (countryBorder.getAttributeValue("country").equals("E")) {
                        double borderLength = Double.parseDouble(countryBorder.getAttributeValue("length"));
                        borderLength -= catBorderLength;
                        Element addBorder = new Element("border");
                        addBorder.setAttribute("country", "CAT");
                        addBorder.setAttribute("length", String.valueOf(catBorderLength));
                        country.addContent(addBorder);

                        if (borderLength <= 0) {
                            country.removeContent(countryBorder);
                        } else {
                            countryBorder.setAttribute("length", String.format(Locale.ENGLISH,"%.0f", borderLength));
                        }
                        break;
                    }
                }
            }

            // 6. Write the updated XML to a new file
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
