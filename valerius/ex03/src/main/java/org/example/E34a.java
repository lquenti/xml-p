package org.example;

import lombok.SneakyThrows;
import org.example.supportClasses.Country;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.Attribute;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class E34a {
    Element spain = null;
    private  final static String provinceId = "prov-Spain-11";

    private static Element ROOT;
    private final static String CATALONIA_XML = "<country car_code=\"CAT\">\n" +
            "  <name>Catalonia</name>\n" +
            "  <population_growth>0.8</population_growth>\n" +
            "  <infant_mortality>2.5</infant_mortality>\n" +
            "  <gdp_total>204189</gdp_total>\n" +
            "  <gdp_agri>3</gdp_agri>\n" +
            "  <gdp_ind>37</gdp_ind>\n" +
            "  <gdp_serv>60</gdp_serv>\n" +
            "  <inflation>1.5</inflation>\n" +
            "  <unemployment>12.6</unemployment>\n" +
            "  <indep_date from=\"E\">2018-04-01</indep_date>\n" +
            "  <ethnicgroup percentage=\"100\">Mediterranean Nordic</ethnicgroup>\n" +
            "  <religion percentage=\"52.4\">Roman Catholic</religion>\n" +
            "  <religion percentage=\"2.5\">Protestant</religion>\n" +
            "  <religion percentage=\"7.3\">Muslim</religion>\n" +
            "  <religion percentage=\"1.3\">Buddhist</religion>\n" +
            "  <religion percentage=\"1.2\">Christian Orthodox</religion>\n" +
            "  <language percentage=\"52\">Spanish</language>\n" +
            "  <language percentage=\"41.5\">Catalan</language>\n" +
            "  <language percentage=\"0.1\">Occitan</language>\n" +
            "  <border country=\"AND\" length=\"65\"/>\n" +
            "  <border country=\"F\" length=\"300\"/>\n" +
            "  <border country=\"E\" length=\"320\"/>\n" +
            "</country>";


    public static Element readCatalonia() {
        try {
            org.jdom2.input.SAXBuilder builder = new org.jdom2.input.SAXBuilder();
            java.io.StringReader reader = new java.io.StringReader(CATALONIA_XML);
            org.jdom2.Document doc = builder.build(reader);
            return doc.getRootElement();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Catalonia XML", e);
        }
    }

    public static Country makeCatalonia() {
        Element cataloniaElement = readCatalonia();
        Country catalonia = new Country();

        catalonia.setName(cataloniaElement.getChild("name").getValue());
        catalonia.setCarCode(cataloniaElement.getAttributeValue("car_code"));
        catalonia.setPopulationGrowth(Double.parseDouble(cataloniaElement.getChild("population_growth").getValue()));
        catalonia.setInfantMortality(Double.parseDouble(cataloniaElement.getChild("infant_mortality").getValue()));
        catalonia.setGdpTotal(Double.parseDouble(cataloniaElement.getChild("gdp_total").getValue()));
        catalonia.setGdpAgri(Double.parseDouble(cataloniaElement.getChild("gdp_agri").getValue()));
        catalonia.setGdpInd(Double.parseDouble(cataloniaElement.getChild("gdp_ind").getValue()));
        catalonia.setGdpServ(Double.parseDouble(cataloniaElement.getChild("gdp_serv").getValue()));
        catalonia.setInflation(Double.parseDouble(cataloniaElement.getChild("inflation").getValue()));
        catalonia.setUnemployment(Double.parseDouble(cataloniaElement.getChild("unemployment").getValue()));
        catalonia.setIndepDate(cataloniaElement.getChild("indep_date").getValue());
        catalonia.setIndepDateFrom(cataloniaElement.getChild("indep_date").getAttributeValue("from"));


        // catalonia.setEthnicGroups(cataloniaElement.getChild("ethnicgroup").getValue());
        // catalonia.setReligions(cataloniaElement.getChild("religion").getValue());
        // catalonia.setLanguages(cataloniaElement.getChild("language").getValue());
        // catalonia.setBorders(cataloniaElement.getChild("border").getValue());
        // catalonia.setProvinces(cataloniaElement.getChild("province").getValue());
        // catalonia.setCities(cataloniaElement.getChild("city").getValue());
        
        return catalonia;
    }

    private Element getProvince(Element root) {
        return root.getChildren("province").stream()
            .filter(province -> provinceId.equals(province.getAttributeValue("id")))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Province not found"));
    }

    private Element getRiver(Element root, String riverId) {
        return root.getChildren("river").stream()
            .filter(river -> riverId.equals(river.getAttributeValue("id")))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("River not found"));
    }

    private Element getMountain(Element root, String mountainId) {
        return root.getChildren("mountain").stream()
            .filter(mountain -> mountainId.equals(mountain.getAttributeValue("id")))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Mountain not found"));
    }

    private Element getSea(Element root, String seaId) {
        return root.getChildren("sea").stream()
            .filter(sea -> seaId.equals(sea.getAttributeValue("id")))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Sea not found"));
    }

    public void run() throws IOException, JDOMException {
        // Read mondial.xml into a JDOM object,
        E34a.ROOT = E31.readMondial();

        // Update it using the JDOM operations
        E34a.ROOT.getChildren("country").stream()
                .filter(country -> "E".equals(country.getAttribute("car_code").getValue()))
                .findFirst().ifPresent(spain -> {
                    this.spain = spain;
                });

        if (spain != null) {
            /* 1. Pull the province
             <province id="prov-Spain-11" country="E" capital="cty-Spain-Barcelona">
                <name>Catalunya</name>
                <name>Cataluña</name>
                <name>Catalonia</name>
                <area>32163</area>
                <population measured="census" year="1981">5956414</population>
                <population measured="census" year="1991">6059443</population>
                <population measured="census" year="2001">6343110</population>
                <population measured="census" year="2011">7519843</population>
                <population measured="census" year="2021">7749896</population>
                <city id="cty-Spain-Barcelona" country="E" province="prov-Spain-11">
                    <name>Barcelona</name>
                    <latitude>41.38</latitude>
                    <longitude>2.18</longitude>
                    <elevation>12</elevation>
                    <population year="1981" measured="census">1752627</population>
                    <population year="1991" measured="census">1643542</population>
                    <population year="2001" measured="census">1503884</population>
                    <population year="2011" measured="census">1611013</population>
                    <population year="2021" measured="census">1627559</population>
                    <located_at watertype="sea" sea="sea-Mittelmeer"/>
                </city>
                <city id="cty-Spain-51" country="E" province="prov-Spain-11">
                    <name>Lleida</name>
                    <latitude>41.62</latitude>
                    <longitude>0.63</longitude> 
                .....
            */


            Element catProvince = getProvince(spain);
            // spain.getChildren("province").stream()
            //     .filter(province -> provinceId.equals(province.getAttributeValue("id")))
            //     .peek(spain::removeContent)
            //     .findFirst()
            //     .orElse(null);
            /* 2. Pull the river            
               <river id="river-Garonne" country="F E">
                    <name>Garonne</name>
                    <located country="F" province="prov-France-5 prov-France-77"/>
                    <located country="E" province="prov-Spain-11"/>
                ....
            */
            if (catProvince != null) {
                System.out.println(catProvince.getChild("name").getValue());
            } else {
                System.out.println("Province not found");
            }

            Element riverGaronne = getRiver(E34a.ROOT, "river-Garonne");
            System.out.println(riverGaronne.getChild("name").getValue());

            Element riverEbro = getRiver(E34a.ROOT, "river-Ebro");
            System.out.println(riverEbro.getChild("name").getValue());
            
            Element riverSegre = getRiver(E34a.ROOT, "river-Segre");
            System.out.println(riverSegre.getChild("name").getValue());

            Element riverValira = getRiver(E34a.ROOT, "river-Valira");
            System.out.println(riverValira.getChild("name").getValue());

            Element mountainEstats = getMountain(E34a.ROOT, "mount-Estats");
            System.out.println(mountainEstats.getChild("name").getValue()); 

            Element mountainComapedrosa = getMountain(E34a.ROOT, "mount-Comapedrosa");
            System.out.println(mountainComapedrosa.getChild("name").getValue());    

            Element mountainCroscat = getMountain(E34a.ROOT, "mount-Croscat");
            System.out.println(mountainCroscat.getChild("name").getValue());    

            Element seaMediterranean = getSea(E34a.ROOT, "sea-Mittelmeer");
            System.out.println(seaMediterranean.getChild("name").getValue());


        } else {
            throw new RuntimeException("Spain not found");
        }

        FileWriter writer = new FileWriter("output_3_4a.xml");
        // Write XML declaration and DOCTYPE manually
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<!DOCTYPE mondial SYSTEM \"mondial.dtd\">\n");
        
        // Output the XML content
        XMLOutputter xmlOutput = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setOmitDeclaration(true);  // Skip XML declaration since we wrote it manually
        xmlOutput.setFormat(format);
        xmlOutput.output(E34a.ROOT, writer);
        writer.close();
    }

    @SneakyThrows
    public static void main(String[] args) {
        E34a ex = new E34a();
        ex.run();
        System.out.println(makeCatalonia());
    }
}
