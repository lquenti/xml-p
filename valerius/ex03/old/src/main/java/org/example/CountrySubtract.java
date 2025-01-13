package org.example;


import lombok.SneakyThrows;
import org.example.sax.MondialHandler;
import org.example.supportClasses.Country;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.List;

public class CountrySubtract {

    // Cities
    // Provinces
    // Borders
    // GSP
    // Populations
    // Neighbours
    // Ethnic Groups
    // Infant Mortalities
    // Religions
    // Area
    // Currency
    // Car Code
    // Capital
    // cities
    // rivers
    // seas

    public List<Country> parse(String filename) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        MondialHandler handler = new MondialHandler();
        saxParser.parse(filename, handler);
        return handler.getCountries();
    }

    public static List<Country> subtract(Country a, Country b) {
        return new ArrayList<>();
    }

    private static void debug(Country a, Country b) {
        System.out.println(a);
        System.out.println(b);
    }

    @SneakyThrows
    public static void main(String[] args) {
        CountrySubtract subtract = new CountrySubtract();
        List<Country> countries = subtract.parse("mondial.xml");
//        for (Country country : countries) {
////            System.out.println(country.getName());
//        }
        debug(countries.get(0), countries.get(1));
    }
}


