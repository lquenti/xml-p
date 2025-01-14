package xmw;

import xmw.mondial.Country;
import xmw.mondial.Mondial;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;

public class App {

    public static Country getCataloniaFromFile(File catdata) throws JAXBException, FileNotFoundException {
        JAXBContext context = JAXBContext.newInstance(Country.class);

        if (!catdata.exists()) {
            throw new FileNotFoundException("not there." + catdata.getAbsolutePath());
        }

        System.setProperty("javax.xml.accessExternalDTD", "all");

        Unmarshaller unmarshaller = context.createUnmarshaller();

        return (Country) unmarshaller.unmarshal(catdata);
    }

    public static void main(String[] args) {
        try {
            Country catalonia = getCataloniaFromFile(new File("catdata.xml"));
            // Create JAXB context for the Mondial class
            JAXBContext context = JAXBContext.newInstance(Mondial.class);

            File inputFile = new File("mondial.xml");
            if (!inputFile.exists()) {
                throw new FileNotFoundException("not there." + inputFile.getAbsolutePath());
            }

            System.setProperty("javax.xml.accessExternalDTD", "all");

            // Create unmarshaller to read XML
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Mondial mondial = (Mondial) unmarshaller.unmarshal(inputFile);

            // Create marshaller to write XML
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", false);
            marshaller.setProperty("com.sun.xml.bind.xmlHeaders",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE mondial SYSTEM \"mondial.dtd\">");

            // Write to output XML file
            File outputFile = new File("output.xml");
            marshaller.marshal(mondial, outputFile);

            System.out.println("XML file has been copied successfully to output.xml");

        } catch (FileNotFoundException e) {
            System.err.println("Input file not found: " + e.getMessage());
            e.printStackTrace();
        } catch (JAXBException e) {
            System.err.println("Error processing XML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}