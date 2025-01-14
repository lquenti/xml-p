package xmw;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import xmw.mondial.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    private static final String TMP_PATH = "tmp.xml";
    private static final String MONDIAL_XML_PATH = "mondial.xml";
    private static final String INDEPENDENCE_XML_PATH = "catdata.xml";
    private static final String NEW_COUNTRY_CAR_CODE = "CAT";
    private static final String OLD_COUNTRY_CAR_CODE = "E"; // countries maybe for basque
    private static final String[] AFFECTED_PROVINCE_IDS = new String[]{"prov-Spain-11"};


    private static Country findCountry(Mondial mondial, String carCode) {
        return mondial.getCountry().stream()
                .filter(c -> carCode.equals(c.getCarCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Catalonia not found"));
    }


    private static Province findProvince(Mondial mondial, String carCode, String id) {
        Country country = findCountry(mondial, carCode);
        return country.getProvince().stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Province not found"));
    }


    public static void updateMissingOnCatalonia(Mondial mondial) {
        Country catalonia = findCountry(mondial, NEW_COUNTRY_CAR_CODE);
        // TODO: make abstract if border is present
        Encompassed encompassed = new Encompassed();
        Continent europe = mondial.getContinent().stream()
                .filter(c -> "europe".equals(c.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Europe not found"));
        encompassed.setContinent(europe);
        encompassed.setPercentage(BigDecimal.valueOf(100.));
        catalonia.getEncompassed().add(encompassed);
    }

    public static void validateXML(File xmlFile) throws SAXException, ParserConfigurationException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser parser = factory.newSAXParser();
        parser.parse(xmlFile, new DefaultHandler());
    }

    public static void main(String[] args) {
        try {
            // TODO: make abstract
            // Create JAXB context for the Mondial class
            JAXBContext context = JAXBContext.newInstance(Mondial.class, Country.class, Border.class);

            File inputFile = new File(MONDIAL_XML_PATH);

            if (!inputFile.exists()) {
                throw new FileNotFoundException("not there." + inputFile.getAbsolutePath());
            }

            File catdataFile = new File(INDEPENDENCE_XML_PATH);
            if (!catdataFile.exists()) {
                throw new FileNotFoundException("not there." + catdataFile.getAbsolutePath());
            }

            // NOTE: to avoid IDREF errors, we need to merge the two XML files into one
            // and add the catdata string after the closing tag of <country car_code="E" ...>
            String mergedXMLRaw = MergeXML.mergeXML(inputFile.getAbsolutePath(), catdataFile.getAbsolutePath(), OLD_COUNTRY_CAR_CODE);
            // save to TMP_PATh
            Files.write(Paths.get(TMP_PATH), mergedXMLRaw.getBytes());
            File mergedXML = new File(TMP_PATH);

            System.setProperty("javax.xml.accessExternalDTD", "all");

            // Create unmarshaller to read XML
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Mondial mondial = (Mondial) unmarshaller.unmarshal(mergedXML);

            // catalonia operations
            updateMissingOnCatalonia(mondial);

            Country spain = findCountry(mondial, OLD_COUNTRY_CAR_CODE);
            Country catalonia = findCountry(mondial, NEW_COUNTRY_CAR_CODE);
            for (String id : AFFECTED_PROVINCE_IDS) {
                // watertypes to update
                List<Sea> seas = new ArrayList<>();
                List<River> rivers = new ArrayList<>();
                List<Lake> lakes = new ArrayList<>();

                Province province = findProvince(mondial, OLD_COUNTRY_CAR_CODE, id);
                spain.getProvince().remove(province);

                // update province to catalonia
                province.setCountry(catalonia);
                for (City city : province.getCity()) {
                    city.setCountry(catalonia);
                    city.setProvince(null);
                    city.getLocatedAt().forEach(l -> {
                        switch (l.getWatertype()) {
                            case "sea":
                                seas.addAll(l.getSea() != null ? l.getSea().stream().map(e -> (Sea) e).collect(Collectors.toList()) : new ArrayList<>());
                                break;
                            case "river":
                                rivers.addAll(l.getRiver() != null ? l.getRiver().stream().map(e -> (River) e).collect(Collectors.toList()) : new ArrayList<>());
                                break;
                            case "lake":
                                lakes.addAll(l.getLake() != null ? l.getLake().stream().map(e -> (Lake) e).collect(Collectors.toList()) : new ArrayList<>());
                                break;
                            default:
                        }
                    });
                }

                // area
                BigDecimal provinceArea = province.getArea();
                BigDecimal countryArea = catalonia.getArea();
                if (countryArea == null) {
                    countryArea = BigDecimal.ZERO;
                }
                catalonia.setArea(countryArea.add(provinceArea));

                // population
                catalonia.getPopulation().addAll(province.getPopulation());
                // subtract population from spain
                for (Population oldPop : spain.getPopulation()) {
                    for (Population newPop : catalonia.getPopulation()) {
                        if (oldPop.getYear() == (newPop.getYear())) {
                            oldPop.setValue(oldPop.getValue().subtract(newPop.getValue()));
                        }
                    }
                }

                // set cities
                catalonia.getCity().addAll(province.getCity());

                // update borders
                for (Border border : catalonia.getBorder()) {
                    Country neighbour = (Country) border.getCountry();
                    List<Border> bordersToRemove = new ArrayList<>();
                    for (Border b : neighbour.getBorder()) {
                        Country currentCountry = (Country) b.getCountry();
                        if (currentCountry.equals(spain)) {
                            b.setLength(b.getLength().subtract(border.getLength()));
                        }
                        if (b.getLength() == null || b.getLength().compareTo(BigDecimal.ZERO) <= 0) {
                            bordersToRemove.add(b);
                        }
                    }
                    neighbour.getBorder().removeAll(bordersToRemove);
                    bordersToRemove.clear();

                    // shorten the border of the neighbour with spain
                    for (Border b : spain.getBorder()) {
                        if (b.getCountry().equals(neighbour)) {
                            b.setLength(b.getLength().subtract(border.getLength()));
                        }
                        if (b.getLength() == null || b.getLength().compareTo(BigDecimal.ZERO) <= 0) {
                            bordersToRemove.add(b);
                        }
                    }
                    spain.getBorder().removeAll(bordersToRemove);

                    Border updatedBorder = new Border();
                    updatedBorder.setLength(border.getLength());
                    updatedBorder.setCountry(catalonia);
                    updatedBorder.setJustice(border.getJustice());
                    neighbour.getBorder().add(updatedBorder);
                }

                // update sea
                for (Sea sea : seas) {
                    if (!catalonia.getProvince().isEmpty()) {
                        Located located = new Located();
                        located.setCountry(catalonia);
                        located.getProvince().addAll(catalonia.getProvince());
                        sea.getLocated().add(located);
                    }
                    List<Located> locatedToRemove = new ArrayList<>();
                    for (Located l : sea.getLocated()) {
                        l.getProvince().remove(province);
                        if (l.getProvince().isEmpty()) {
                            locatedToRemove.add(l);
                        }
                    }
                    sea.getLocated().removeAll(locatedToRemove);
                    if (!sea.getCountry().contains(catalonia)) {
                        sea.getCountry().add(catalonia);
                    }
                }

                // find rivers which are affected by the province
                List<River> affectedRivers = new ArrayList<>();
                for (River river : mondial.getRiver()) {
                    if (river.getCountry().contains(spain)) {
                        boolean bLocated = river.getLocated().stream().anyMatch(l -> l.getProvince().contains(province));
                        boolean bSource = river.getSource().getLocated().stream().anyMatch(l -> l.getProvince().contains(province));
                        boolean bEstuary = river.getEstuary().getLocated().stream().anyMatch(l -> l.getProvince().contains(province));
                        if (bLocated || bSource || bEstuary) {
                            affectedRivers.add(river);
                        }
                    }
                }

                for (River river : affectedRivers)
                    if (!rivers.contains(river)) rivers.add(river);

                // update rivers
                for (River river : rivers) {
                    // update country
                    river.getCountry().add(catalonia);
                    // update located
                    List<Located> locatedToRemove = new ArrayList<>();
                    for (Located l : river.getLocated()) {
                        if (l.getProvince().contains(province)) {
                            l.getProvince().remove(province);
                            if (l.getProvince().isEmpty()) {
                                locatedToRemove.add(l);
                            }
                        }
                    }
                    river.getLocated().removeAll(locatedToRemove);
                    locatedToRemove.clear();
                    // update estuary

                    // update estuary located
                    for (Located estLocated : river.getEstuary().getLocated()) {
                        estLocated.getProvince().remove(province);
                        if (estLocated.getProvince().isEmpty()) {
                            locatedToRemove.add(estLocated);
                        }
                    }

                    // update estuary country
                    river.getEstuary().getLocated().removeAll(locatedToRemove);
                    if(river.getEstuary().getLocated().isEmpty()) {
                        river.getEstuary().getCountry().remove(spain);
                        river.getEstuary().getCountry().add(catalonia);
                    }
                    locatedToRemove.clear();

                    // update source
                    for (Located srcLocated : river.getSource().getLocated()) {
                        srcLocated.getProvince().remove(province);
                        if (srcLocated.getProvince().isEmpty()) {
                            locatedToRemove.add(srcLocated);
                        }
                    }

                    // update source country
                    river.getSource().getLocated().removeAll(locatedToRemove);
                    if(river.getSource().getLocated().isEmpty()) {
                        river.getSource().getCountry().remove(spain);
                        river.getSource().getCountry().add(catalonia);
                    }

                    river.getCountry().clear();
                    river.getCountry().addAll(river.getSource().getCountry());
                    for (Country c : river.getEstuary().getCountry().stream().map(e -> (Country) e).collect(Collectors.toList())) {
                        if (!river.getCountry().contains(c)) {
                            river.getCountry().add(c);
                        }
                    }
                }

                // update mountains
                for(Mountain mountain : mondial.getMountain()) {
                    if(mountain.getCountry().contains(spain) && !mountain.getLocated().isEmpty()) {
                        Located l = mountain.getLocated().stream().filter(e -> e.getProvince().contains(province)).findFirst().orElse(null);
                        if (l != null) {
                            l.getProvince().remove(province);
                            boolean locatedRemoved = false;
                            if (l.getProvince().isEmpty()) {
                                mountain.getLocated().remove(l);
                                locatedRemoved = true;
                            }
                            mountain.getCountry().add(catalonia);
                            if (locatedRemoved) {
                                mountain.getCountry().remove(spain);
                            }
                        }
                    }
                }
            }


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
            // delete TMP_PATH
            File tmpFile = new File(TMP_PATH);
            if (tmpFile.exists() && !tmpFile.delete()) {
                System.err.println("Failed to delete temporary file: " + TMP_PATH);
                System.exit(1);
            }
        }
    }
}