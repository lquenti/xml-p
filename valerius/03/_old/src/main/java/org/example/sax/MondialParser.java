package org.example.sax;

import org.example.supportClasses.Country;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;

    public class MondialParser {
        public List<Country> parse(String filename) throws Exception {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            MondialHandler handler = new MondialHandler();
            saxParser.parse(filename, handler);
            return handler.getCountries();
        }
}
