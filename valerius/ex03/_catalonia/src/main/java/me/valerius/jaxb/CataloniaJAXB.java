package me.valerius.jaxb;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import me.valerius.jaxb.model.Airport;
import me.valerius.jaxb.model.City;
import me.valerius.jaxb.model.Continent;
import me.valerius.jaxb.model.Country;
import me.valerius.jaxb.model.Desert;
import me.valerius.jaxb.model.Island;
import me.valerius.jaxb.model.Lake;
import me.valerius.jaxb.model.LangTree;
import me.valerius.jaxb.model.Located;
import me.valerius.jaxb.model.LocatedOn;
import me.valerius.jaxb.model.Member;
import me.valerius.jaxb.model.Mondial;
import me.valerius.jaxb.model.Mountain;
import me.valerius.jaxb.model.Organization;
import me.valerius.jaxb.model.Province;
import me.valerius.jaxb.model.River;
import me.valerius.jaxb.model.Sea;
import me.valerius.jaxb.model.SpokenBy;

public class CataloniaJAXB {
    public static final String SPAIN_CAR_CODE = "E";
    public static final String PROVINCE_TO_DETACH = "prov-Spain-11";

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.xml.accessExternalDTD", "all");

        // Create JAXB contexts
        JAXBContext mondialContext = JAXBContext.newInstance(
                Mondial.class, Country.class, Continent.class, Organization.class,
                Sea.class, River.class, Lake.class, Island.class, Mountain.class,
                Desert.class, Airport.class, LangTree.class, Member.class,
                Located.class, LocatedOn.class, SpokenBy.class);
        JAXBContext countryContext = JAXBContext.newInstance(Country.class);

        // Create unmarshallers
        Unmarshaller mondialUnmarshaller = mondialContext.createUnmarshaller();
        Unmarshaller countryUnmarshaller = countryContext.createUnmarshaller();

        // Create marshaller without schema validation
        Marshaller marshaller = mondialContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("org.glassfish.jaxb.xmlDeclaration", Boolean.FALSE);
        marshaller.setProperty("org.glassfish.jaxb.xmlHeaders",
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<!DOCTYPE mondial SYSTEM \"mondial.dtd\">\n");

        // Read mondial.xml
        Mondial mondial = (Mondial) mondialUnmarshaller.unmarshal(new File("mondial.xml"));

        // Read catdata.xml
        Country cataloniaTemplate = (Country) countryUnmarshaller.unmarshal(new File("catdata.xml"));

        // Process the data
        XMLProcessor processor = new XMLProcessor();
        processor.process(mondial, cataloniaTemplate);

        // Write the output
        marshaller.marshal(mondial, new File("output_jaxb.xml"));
    }
}

class XMLProcessor {
    public void process(Mondial mondial, Country cataloniaTemplate) {
        // Find Spain and extract Catalonia province
        Country spain = findSpain(mondial);
        if (spain == null)
            return;

        Province catalonia = extractCataloniaProvince(spain);
        if (catalonia == null)
            return;

        // Create new Catalonia country
        Country cataloniaCountry = createCataloniaCountry(cataloniaTemplate, catalonia, "");
        mondial.getCountries().add(cataloniaCountry);

        // Update geographical features
        updateSeas(mondial, catalonia);
        updateRivers(mondial, catalonia);
        updateCities(mondial, catalonia);
    }

    private Country findSpain(Mondial mondial) {
        return mondial.getCountries().stream()
                .filter(c -> CataloniaJAXB.SPAIN_CAR_CODE.equals(c.getCarCode()))
                .findFirst()
                .orElse(null);
    }

    private Province extractCataloniaProvince(Country spain) {
        Province catalonia = null;
        for (Province province : spain.getProvinces()) {
            if (CataloniaJAXB.PROVINCE_TO_DETACH.equals(province.getId())) {
                catalonia = province;
                spain.getProvinces().remove(province);
                break;
            }
        }
        return catalonia;
    }

    private Country createCataloniaCountry(Country template, Province catalonia, String memberships) {
        Country country = new Country();
        country.setCarCode("CAT");
        country.setArea(catalonia.getArea());
        // Don't copy memberships as they reference organizations not in our output
        country.setMemberships("");
        country.setNames(template.getNames());
        country.setEncompassed(template.getEncompassed());

        // Add the province
        catalonia.setCountry("CAT");
        country.getProvinces().add(catalonia);

        // Copy other elements from template
        country.setEthnicGroups(template.getEthnicGroups());
        country.setReligions(template.getReligions());
        country.setLanguages(template.getLanguages());

        return country;
    }

    private void updateSeas(Mondial mondial, Province catalonia) {
        for (Sea sea : mondial.getSeas()) {
            if ("sea-Mittelmeer".equals(sea.getId())) {
                String countries = sea.getCountry();
                if (!countries.contains("CAT")) {
                    sea.setCountry("CAT " + countries);
                }
            }
        }
    }

    private void updateRivers(Mondial mondial, Province catalonia) {
        for (River river : mondial.getRivers()) {
            String countries = river.getCountry();
            if (countries != null && countries.contains("E")) {
                // Check if river has Catalonian province
                boolean hasCatalonianProvince = river.getLocated().stream()
                        .anyMatch(loc -> CataloniaJAXB.PROVINCE_TO_DETACH.equals(loc.getProvince()));

                if (hasCatalonianProvince) {
                    // Update river country
                    river.setCountry(countries.replace("E", "CAT"));

                    // Update source country if it's Spain
                    if (river.getSource() != null && "E".equals(river.getSource().getCountry())) {
                        river.getSource().setCountry("CAT");
                    }

                    // Update estuary country if it's Spain
                    if (river.getEstuary() != null && "E".equals(river.getEstuary().getCountry())) {
                        river.getEstuary().setCountry("CAT");
                    }
                }
            }
        }
    }

    private void updateCities(Mondial mondial, Province catalonia) {
        // First, collect all cities from all countries and provinces, using a map to
        // prevent duplicates
        Map<String, City> cityMap = new HashMap<>();
        for (Country country : mondial.getCountries()) {
            for (Province province : country.getProvinces()) {
                for (City city : province.getCities()) {
                    cityMap.putIfAbsent(city.getId(), city);
                }
            }
        }

        // Update cities in Catalonia to reference CAT instead of E
        for (City city : catalonia.getCities()) {
            city.setCountry("CAT");
            // Make sure the updated Catalonian cities are in the map
            cityMap.put(city.getId(), city);
        }

        // Add all unique cities to mondial
        mondial.setCities(new ArrayList<>(cityMap.values()));

        // Update airports to reference the correct cities
        for (Airport airport : mondial.getAirports()) {
            if ("E".equals(airport.getCountry())) {
                // Check if the airport's city is in Catalonia
                String cityId = airport.getCity();
                if (cityId != null) {
                    boolean inCatalonia = catalonia.getCities().stream()
                            .anyMatch(city -> cityId.equals(city.getId()));

                    if (inCatalonia) {
                        airport.setCountry("CAT");
                    }
                }
            }
        }
    }
}