package org.example.cat.sax;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.sf.saxon.functions.Count;
import org.example.supportClasses.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

@Getter
@Setter
@ToString
public class CatHandler extends DefaultHandler {
    private boolean insideCountry = false;
    private boolean insideSpain = false;
    private StringBuilder currentValue = new StringBuilder();
    private Country spain;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        // Clear the current value
        currentValue.setLength(0);
        
        if ("country".equals(qName)) {
            insideCountry = true;
            String carCode = attributes.getValue("car_code");
            if ("E".equals(carCode)) {
                insideSpain = true;

                spain = new Country();
                spain.setCarCode(carCode);
                spain.setArea(Double.parseDouble(attributes.getValue("area")));
                if (attributes.getValue("capital") != null) {
                    spain.setCapital(attributes.getValue("capital"));
                }
                if (attributes.getValue("memberships") != null) {
                    spain.setMemberships(attributes.getValue("memberships"));
                }
            } else {
                insideSpain = false;
            }
        } else if (insideCountry && insideSpain) {
            System.out.println(qName);
            switch (qName) {
                case "population":
                    Population pop = new Population();
                    pop.setYear(Integer.parseInt(attributes.getValue("year")));
                    if (attributes.getValue("measured") != null) {
                        pop.setMeasured(attributes.getValue("measured"));
                    }
                    spain.getPopulations().add(pop);
                    break;
                case "encompassed":
                    Encompassed enc = new Encompassed();
                    enc.setContinent(attributes.getValue("continent"));
                    enc.setPercentage(Double.parseDouble(attributes.getValue("percentage")));
                    spain.getEncompassed().add(enc);
                    break;
                case "border":
                    Border border = new Border();
                    border.setCountry(attributes.getValue("country"));
                    border.setLength(Double.parseDouble(attributes.getValue("length")));
                    spain.getBorders().add(border);
                    break;
                case "ethnicgroup":
                case "religion":
                case "language":
                    if (attributes.getValue("percentage") != null) {
                        Percentage perc = new Percentage();
                        perc.setPercentage(Double.parseDouble(attributes.getValue("percentage")));
                        switch (qName) {
                            case "ethnicgroup":
                                EthnicGroup ethnicGroup = new EthnicGroup();
                                ethnicGroup.setPercentage(perc.getPercentage());
                                spain.getEthnicGroups().add(ethnicGroup);
                                break;
                            case "religion":
                                Religion religion = new Religion();
                                religion.setPercentage(perc.getPercentage());
                                spain.getReligions().add(religion);
                                break;
                            case "language":
                                Language language = new Language();
                                language.setPercentage(perc.getPercentage());
                                spain.getLanguages().add(language);
                                break;
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        currentValue.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (insideSpain && "name".equals(qName)) {
            spain.setName(currentValue.toString().trim());
        }
    }
}
