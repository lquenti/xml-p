package xmw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import xmw.mondial.Border;
import xmw.mondial.Continent;
import xmw.mondial.Country;
import xmw.mondial.Encompassed;
import xmw.mondial.Mondial;

public class App {



//    public static void updateMissingOnCatalonia(Mondial mondial, Country catalonia) {
//        Encompassed encompassed = new Encompassed();
//        Continent europe = mondial.getContinent().stream()
//                .filter(c -> "europe".equals(c.getId()))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("Europe not found"));
//        encompassed.setContinent(europe);
//        encompassed.setPercentage(BigDecimal.valueOf(100.));
//        catalonia.getEncompassed().add(encompassed);
//    }

    public static void validateXML(File xmlFile) throws SAXException, ParserConfigurationException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser parser = factory.newSAXParser();
        parser.parse(xmlFile, new DefaultHandler());
    }

    public static void main(String[] args) {
        try {
            // Create JAXB context for the Mondial class
            JAXBContext context = JAXBContext.newInstance(Mondial.class, Country.class, Border.class);

            File inputFile = new File("mondial.xml");

            if (!inputFile.exists()) {
                throw new FileNotFoundException("not there." + inputFile.getAbsolutePath());
            }

            File catdataFile = new File("catdata.xml");
            if (!catdataFile .exists()) {
                throw new FileNotFoundException("not there." + catdataFile .getAbsolutePath());
            }

            // NOTE: to avoid IDREF errors, we need to merge the two XML files into one
            // and add the catdata string after the closing tag of <country car_code="E" ...>
            String mergedXMLRaw = MergeXML.mergeXML(inputFile.getAbsolutePath(), catdataFile.getAbsolutePath());
            // save to "tmp.xml"
            Files.write(Paths.get("tmp.xml"), mergedXMLRaw.getBytes());
            File mergedXML = new File("tmp.xml");

            System.setProperty("javax.xml.accessExternalDTD", "all");

            // Create unmarshaller to read XML
            Unmarshaller unmarshaller = context.createUnmarshaller();

            Mondial mondial = (Mondial) unmarshaller.unmarshal(mergedXML);

//
//            updateMissingOnCatalonia(mondial, catalonia);
//            mondial.getCountry().add(catalonia);

            // Create marshaller to write XML
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.bind.xmlDeclaration", false);
            marshaller.setProperty("com.sun.xml.bind.xmlHeaders",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE mondial SYSTEM \"mondial.dtd\">");

            // Write to output XML file
            File outputFile = new File("output.xml");
            marshaller.marshal(mondial, outputFile);

            // Validate the output XML file
            validateXML(outputFile);

            System.out.println("XML file has been written successfully to output.xml");

        } catch (SAXException e) {
            System.err.println("Error parsing XML: " + e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found: " + e.getMessage());
            e.printStackTrace();
        } catch (JAXBException e) {
            System.err.println("Error processing XML: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {

            // delete tmp.xml
            File tmpFile = new File("tmp.xml");
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }
}