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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JaxbIndependenceProcessor {
    private static final String TMP_PATH = "tmp.xml";
    private final String mondialXmlPath;
    private final String independenceXmlPath;
    private final String newCountryCarCode;
    private final Map<String, Double> newCountryContinents;
    private final String oldCountryCarCode; // countries maybe for basque
    private final String[] affectedProvinceIds;

    public static void main(String[] args) {
        try {
            JaxbIndependenceProcessor processor = new JaxbIndependenceProcessor();
            processor.process();
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
            clearTempFile();
        }
    }

    public JaxbIndependenceProcessor() {
        mondialXmlPath = "mondial.xml";
        independenceXmlPath = "catdata.xml";
        newCountryCarCode = "CAT";

        newCountryContinents = new HashMap<>();
        newCountryContinents.put("europe", 100.);

        oldCountryCarCode = "E";
        affectedProvinceIds = new String[]{"prov-Spain-11"};
    }

    public JaxbIndependenceProcessor(String mondialXmlPath, String independenceXmlPath, String newCountryCarCode,
                                     Map<String, Double> newCountryContinents, String oldCountryCarCode,
                                     String[] affectedProvinceIds) {
        this.mondialXmlPath = mondialXmlPath;
        this.independenceXmlPath = independenceXmlPath;
        this.newCountryCarCode = newCountryCarCode;
        this.newCountryContinents = newCountryContinents;
        this.oldCountryCarCode = oldCountryCarCode;
        this.affectedProvinceIds = affectedProvinceIds;
    }

    private static Country findCountry(Mondial mondial, String carCode) {
        return mondial.getCountry().stream()
                .filter(c -> carCode.equals(c.getCarCode()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("New Country not found"));
    }


    private static Province findProvince(Mondial mondial, String carCode, String id) {
        Country country = findCountry(mondial, carCode);
        return country.getProvince().stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Province not found"));
    }


    public void updateMissingOnNewCountry(Mondial mondial) {
        Country newCountry = findCountry(mondial, newCountryCarCode);

        List<Encompassed> encompassedList = new ArrayList<>();

        for (String targetContinent : newCountryContinents.keySet()) {
            Continent continent = mondial.getContinent().stream()
                    .filter(c -> targetContinent.equals(c.getId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Continent not found"));
            Encompassed encompassed = new Encompassed();
            encompassed.setContinent(continent);
            encompassed.setPercentage(BigDecimal.valueOf(newCountryContinents.get(targetContinent)));
            encompassedList.add(encompassed);
        }

        newCountry.getEncompassed().addAll(encompassedList);
    }

    public static void validateXML(File xmlFile) throws SAXException, ParserConfigurationException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        SAXParser parser = factory.newSAXParser();
        parser.parse(xmlFile, new DefaultHandler());
    }


    private static void clearTempFile() {
        // delete TMP_PATH
        File tmpFile = new File(TMP_PATH);
        if (tmpFile.exists() && !tmpFile.delete()) {
            System.err.println("Failed to delete temporary file: " + TMP_PATH);
            System.exit(1);
        }
    }

    private void process() throws JAXBException, IOException, SAXException, ParserConfigurationException {
        // Create JAXB context for the Mondial class
        JAXBContext context = JAXBContext.newInstance(Mondial.class, Country.class, Border.class);

        File inputFile = new File(mondialXmlPath);

        if (!inputFile.exists()) {
            throw new FileNotFoundException("not there." + inputFile.getAbsolutePath());
        }

        File catdataFile = new File(independenceXmlPath);
        if (!catdataFile.exists()) {
            throw new FileNotFoundException("not there." + catdataFile.getAbsolutePath());
        }

        // NOTE: to avoid IDREF errors, we need to merge the two XML files into one
        // and add the catdata string after the closing tag of <country car_code="E" ...>
        String mergedXMLRaw = MergeXML.mergeXML(inputFile.getAbsolutePath(), catdataFile.getAbsolutePath(), oldCountryCarCode);
        // save to TMP_PATh
        Files.write(Paths.get(TMP_PATH), mergedXMLRaw.getBytes());
        File mergedXML = new File(TMP_PATH);

        System.setProperty("javax.xml.accessExternalDTD", "all");

        // Create unmarshaller to read XML
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Mondial mondial = (Mondial) unmarshaller.unmarshal(mergedXML);

        // newCountry operations
        updateMissingOnNewCountry(mondial);

        Country oldCountry = findCountry(mondial, oldCountryCarCode);
        Country newCountry = findCountry(mondial, newCountryCarCode);

        for (String id : affectedProvinceIds) {
            processProvince(id, mondial, oldCountry, newCountry);
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
    }

    private void processProvince(String id, Mondial mondial, Country oldCountry, Country newCountry) {
        // water types to update
        List<Sea> seas = new ArrayList<>();
        List<River> rivers = new ArrayList<>();
        List<Lake> lakes = new ArrayList<>();

        Province province = findProvince(mondial, oldCountryCarCode, id);
        oldCountry.getProvince().remove(province);

        // update province to newCountry
        province.setCountry(newCountry);
        moveCityToTopLayer(province, newCountry, seas, rivers, lakes);

        // area
        setAreaForNewCountry(province, newCountry);

        // population
        updatePopulation(newCountry, province, oldCountry);

        // update borders
        updateBoarders(newCountry, oldCountry);

        // update sea
        updateSeas(seas, newCountry, province);

        // find rivers which are affected by the province
        findAffectedRivers(mondial, oldCountry, province, rivers);

        // update rivers
        updateRivers(rivers, newCountry, province, oldCountry);

        // update mountains
        updateMountains(mondial, oldCountry, province, newCountry);
    }

    private static void updateMountains(Mondial mondial, Country oldCountry, Province province, Country newCountry) {
        for (Mountain mountain : mondial.getMountain()) {
            if (mountain.getCountry().contains(oldCountry) && !mountain.getLocated().isEmpty()) {
                Located l = mountain.getLocated().stream().filter(e -> e.getProvince().contains(province)).findFirst().orElse(null);
                if (l != null) {
                    l.getProvince().remove(province);
                    boolean locatedRemoved = false;
                    if (l.getProvince().isEmpty()) {
                        mountain.getLocated().remove(l);
                        locatedRemoved = true;
                    }
                    mountain.getCountry().add(newCountry);
                    if (locatedRemoved) {
                        mountain.getCountry().remove(oldCountry);
                    }
                }
            }
        }
    }

    private static void updateRivers(List<River> rivers, Country newCountry, Province province, Country oldCountry) {
        for (River river : rivers) {
            // update country
            river.getCountry().add(newCountry);

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
            if (river.getEstuary().getLocated().isEmpty()) {
                river.getEstuary().getCountry().remove(oldCountry);
                river.getEstuary().getCountry().add(newCountry);
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
            if (river.getSource().getLocated().isEmpty()) {
                river.getSource().getCountry().remove(oldCountry);
                river.getSource().getCountry().add(newCountry);
            }

            river.getCountry().clear();
            river.getCountry().addAll(river.getSource().getCountry());

            // remove dangling countries
            for (Country c : river.getEstuary().getCountry().stream().map(e -> (Country) e).collect(Collectors.toList())) {
                if (!river.getCountry().contains(c)) {
                    river.getCountry().add(c);
                }
            }
        }
    }

    private static void findAffectedRivers(Mondial mondial, Country oldCountry, Province province, List<River> rivers) {
        List<River> affectedRivers = new ArrayList<>();
        for (River river : mondial.getRiver()) {
            if (river.getCountry().contains(oldCountry)) {
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
    }

    private static void updateSeas(List<Sea> seas, Country newCountry, Province province) {
        for (Sea sea : seas) {
            if (!newCountry.getProvince().isEmpty()) {
                Located located = new Located();
                located.setCountry(newCountry);
                located.getProvince().addAll(newCountry.getProvince());
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
            if (!sea.getCountry().contains(newCountry)) {
                sea.getCountry().add(newCountry);
            }
        }
    }

    private static void updateBoarders(Country newCountry, Country oldCountry) {
        for (Border border : newCountry.getBorder()) {
            Country neighbour = (Country) border.getCountry();
            List<Border> bordersToRemove = new ArrayList<>();
            for (Border b : neighbour.getBorder()) {
                Country currentCountry = (Country) b.getCountry();
                if (currentCountry.equals(oldCountry)) {
                    b.setLength(b.getLength().subtract(border.getLength()));
                }
                if (b.getLength() == null || b.getLength().compareTo(BigDecimal.ZERO) <= 0) {
                    bordersToRemove.add(b);
                }
            }
            neighbour.getBorder().removeAll(bordersToRemove);
            bordersToRemove.clear();

            // shorten the border of the neighbour with oldCountry
            for (Border b : oldCountry.getBorder()) {
                if (b.getCountry().equals(neighbour)) {
                    b.setLength(b.getLength().subtract(border.getLength()));
                }
                if (b.getLength() == null || b.getLength().compareTo(BigDecimal.ZERO) <= 0) {
                    bordersToRemove.add(b);
                }
            }
            oldCountry.getBorder().removeAll(bordersToRemove);

            Border updatedBorder = new Border();
            updatedBorder.setLength(border.getLength());
            updatedBorder.setCountry(newCountry);
            updatedBorder.setJustice(border.getJustice());
            neighbour.getBorder().add(updatedBorder);
        }
    }

    private static void updatePopulation(Country newCountry, Province province, Country oldCountry) {
        newCountry.getPopulation().addAll(province.getPopulation());
        // subtract population from oldCountry
        for (Population oldPop : oldCountry.getPopulation()) {
            for (Population newPop : newCountry.getPopulation()) {
                if (oldPop.getYear() == (newPop.getYear())) {
                    oldPop.setValue(oldPop.getValue().subtract(newPop.getValue()));
                }
            }
        }

        // set cities
        newCountry.getCity().addAll(province.getCity());
    }

    private static void setAreaForNewCountry(Province province, Country newCountry) {
        BigDecimal provinceArea = province.getArea();
        BigDecimal countryArea = newCountry.getArea();
        if (countryArea == null) {
            countryArea = BigDecimal.ZERO;
        }
        newCountry.setArea(countryArea.add(provinceArea));
    }

    private static void moveCityToTopLayer(Province province, Country newCountry, List<Sea> seas, List<River> rivers, List<Lake> lakes) {
        for (City city : province.getCity()) {
            city.setCountry(newCountry);
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
    }
}