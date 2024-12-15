package org.example.sax;

import org.example.supportClasses.Country;
import org.example.supportClasses.Population;
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
            insideCountry = true;
        } else if (insideCountry) {
            // Handle nested elements
            if ("population".equals(qName)) {
                Population pop = new Population();
                pop.setYear(Integer.parseInt(attributes.getValue("year")));
                if (attributes.getValue("measured") != null) {
                    pop.setMeasured(attributes.getValue("measured"));
                }
                currentCountry.getPopulations().add(pop);
            }
            // Add similar handling for other nested elements
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        currentValue.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (insideCountry) {
            switch (qName) {
                case "name":
                    currentCountry.setName(currentValue.toString().trim());
                    break;
                case "localname":
                    currentCountry.setLocalName(currentValue.toString().trim());
                    break;
                case "population":
                    if (!currentCountry.getPopulations().isEmpty()) {
                        currentCountry.getPopulations().get(
                                        currentCountry.getPopulations().size() - 1)
                                .setValue(Integer.parseInt(currentValue.toString().trim()));
                    }
                    break;
                case "population_growth":
                    currentCountry.setPopulationGrowth(
                            Double.parseDouble(currentValue.toString().trim()));
                    break;
                // Add cases for other elements
                case "country":
                    countries.add(currentCountry);
                    currentCountry = null;
                    insideCountry = false;
                    break;
            }
        }
    }

    public List<Country> getCountries() {
        return countries;
    }
}
