package org.example.sax;

import org.example.supportClasses.Border;
import org.example.supportClasses.Country;
import org.example.supportClasses.Encompassed;
import org.example.supportClasses.EthnicGroup;
import org.example.supportClasses.Language;
import org.example.supportClasses.Percentage;
import org.example.supportClasses.Population;
import org.example.supportClasses.Religion;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class MondialHandler extends DefaultHandler {
    private Country currentCountry;
    private List<Country> countries = new ArrayList<>();
    private StringBuilder currentValue = new StringBuilder();
    private boolean insideCountry = false;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentValue.setLength(0);

        if ("country".equals(qName)) {
            currentCountry = new Country();
            currentCountry.setCarCode(attributes.getValue("car_code"));
            currentCountry.setArea(Double.parseDouble(attributes.getValue("area")));
            if (attributes.getValue("capital") != null) {
                currentCountry.setCapital(attributes.getValue("capital"));
            }
            if (attributes.getValue("memberships") != null) {
                currentCountry.setMemberships(attributes.getValue("memberships"));
            }
            insideCountry = true;
        } else if (insideCountry) {
            // Handle nested elements
            switch (qName) {
                case "population":
                    Population pop = new Population();
                    pop.setYear(Integer.parseInt(attributes.getValue("year")));
                    if (attributes.getValue("measured") != null) {
                        pop.setMeasured(attributes.getValue("measured"));
                    }
                    currentCountry.getPopulations().add(pop);
                    break;
                case "encompassed":
                    Encompassed enc = new Encompassed();
                    enc.setContinent(attributes.getValue("continent"));
                    enc.setPercentage(Double.parseDouble(attributes.getValue("percentage")));
                    currentCountry.getEncompassed().add(enc);
                    break;
                case "border":
                    Border border = new Border();
                    border.setCountry(attributes.getValue("country"));
                    border.setLength(Double.parseDouble(attributes.getValue("length")));
                    currentCountry.getBorders().add(border);
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
                                currentCountry.getEthnicGroups().add(ethnicGroup);
                                break;
                            case "religion":
                                Religion religion = new Religion();
                                religion.setPercentage(perc.getPercentage());
                                currentCountry.getReligions().add(religion);
                                break;
                            case "language":
                                Language language = new Language();
                                language.setPercentage(perc.getPercentage());
                                currentCountry.getLanguages().add(language);
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
        if (insideCountry) {
            String value = currentValue.toString().trim();
            if (!value.isEmpty()) {
                switch (qName) {
                    case "name":
                        currentCountry.setName(value);
                        break;
                    case "localname":
                        currentCountry.setLocalName(value);
                        break;
                    case "population":
                        if (!currentCountry.getPopulations().isEmpty()) {
                            currentCountry.getPopulations().get(
                                    currentCountry.getPopulations().size() - 1)
                                    .setValue(Integer.parseInt(value));
                        }
                        break;
                    case "population_growth":
                        currentCountry.setPopulationGrowth(Double.parseDouble(value));
                        break;
                    case "infant_mortality":
                        currentCountry.setInfantMortality(Double.parseDouble(value));
                        break;
                    case "gdp_total":
                        currentCountry.setGdpTotal(Double.parseDouble(value));
                        break;
                    case "gdp_agri":
                        currentCountry.setGdpAgri(Double.parseDouble(value));
                        break;
                    case "gdp_ind":
                        currentCountry.setGdpInd(Double.parseDouble(value));
                        break;
                    case "gdp_serv":
                        currentCountry.setGdpServ(Double.parseDouble(value));
                        break;
                    case "inflation":
                        currentCountry.setInflation(Double.parseDouble(value));
                        break;
                    case "unemployment":
                        currentCountry.setUnemployment(Double.parseDouble(value));
                        break;
                    case "government":
                        currentCountry.setGovernment(value);
                        break;
                    case "ethnicgroup":
                        if (!currentCountry.getEthnicGroups().isEmpty()) {
                            currentCountry.getEthnicGroups().get(
                                    currentCountry.getEthnicGroups().size() - 1)
                                    .setName(value);
                        }
                        break;
                    case "religion":
                        if (!currentCountry.getReligions().isEmpty()) {
                            currentCountry.getReligions().get(
                                    currentCountry.getReligions().size() - 1)
                                    .setName(value);
                        }
                        break;
                    case "language":
                        if (!currentCountry.getLanguages().isEmpty()) {
                            currentCountry.getLanguages().get(
                                    currentCountry.getLanguages().size() - 1)
                                    .setName(value);
                        }
                        break;
                    case "country":
                        countries.add(currentCountry);
                        currentCountry = null;
                        insideCountry = false;
                        break;
                }
            }
        }
    }

    public List<Country> getCountries() {
        return countries;
    }
}
