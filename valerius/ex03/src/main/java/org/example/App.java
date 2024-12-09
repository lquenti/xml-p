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
            E31.run();
            System.out.println("======================");
            E34.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JDOMException e) {
            throw new RuntimeException(e);
        }
    }
}
