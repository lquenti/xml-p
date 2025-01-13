package org.example.cat.sax;

import lombok.SneakyThrows;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CatParser {

    @SneakyThrows
    public static void main(String[] args) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        CatHandler catHandler = new CatHandler();
        parser.parse("mondial.xml", catHandler);
        System.out.println(catHandler.getSpain());
    }
}
