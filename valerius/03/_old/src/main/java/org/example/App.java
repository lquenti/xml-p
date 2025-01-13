package org.example;

import org.jdom2.JDOMException;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static final String MONDIAL_XML_PATH = "./mondial.xml";
    public static void main( String[] args )
    {
        try {
            System.out.println("=========3.1=========");
            E31.run();
            System.out.println("=========3.4=========");
            E34.run();
            System.out.println("Done");
            System.out.println("=========3.5=========");
            E35.run();
            System.out.println("Done");
            System.out.println("=========3.6=========");
            E36.run();
            System.out.println("Done");
            System.out.println("=========3.6a=========");
//            E36a.run();
            System.out.println("Done");
            System.out.println("=========3.7=========");
            E37.run();
            System.out.println("Done");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
