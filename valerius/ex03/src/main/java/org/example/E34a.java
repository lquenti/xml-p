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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class E34a {
    Element spain = null;
    private  final static String provinceId = "prov-Spain-11";
    private final static HashMap<String, String> replaceMap = new HashMap<>();
    static {
        replaceMap.put("prov-Spain-11", "prov-Catalonia-1");
        replaceMap.put("cty-Spain-Barcelona", "cty-Catalonia-Barcelona");
    }

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


    public static Element readCataloniaIntoElement() {
        try {
            org.jdom2.input.SAXBuilder builder = new org.jdom2.input.SAXBuilder();
            java.io.StringReader reader = new java.io.StringReader(CATALONIA_XML);
            org.jdom2.Document doc = builder.build(reader);
            return doc.getRootElement();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Catalonia XML", e);
        }
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

    private Element mutateProvince(Element province) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private Element mutateCountry(Element country) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private Element mutateRiver(Element river) {
        river.setAttribute("country", "CAT");
        List<Element> located = river.getChildren("located");
        for (Element l : located) {
            l.setAttribute("country", l.getAttributeValue("country").replace("E", "CAT"));
            l.setAttribute("province", l.getAttributeValue("province").replace("prov-Spain-11", "prov-Catalonia-1"));
        }
        Element source = river.getChild("source");
        if (source != null && source.getAttributeValue("country").equals("E")   ) {
            source.setAttribute("country", "CAT");
            source.getChild("located").setAttribute("province", "prov-Catalonia-1").setAttribute("country", "CAT");
        }
        return river;
    }

    private Element mutateMountain(Element mountain) {
        mountain.setAttribute("country", "CAT");
        List<Element> located = mountain.getChildren("located");
        for (Element l : located) {
            l.setAttribute("country", l.getAttributeValue("country").replace("E", "CAT"));
            l.setAttribute("province", l.getAttributeValue("province").replace("prov-Spain-11", "prov-Catalonia-1"));
        }
        return mountain;
    }

    private Element mutateSea(Element sea) {
        // <located country="CAT" province="prov-Catalonia-1" >
        Element located = new Element("located")
            .setAttribute("country", "CAT")
            .setAttribute("province", "prov-Catalonia-1");
        sea.addContent(located);

        // change <located country="E" province="prov-Spain-2 prov-Spain-5 prov-Spain-11 prov-Spain-15 prov-Spain-18" />
        // to <located country="E" province="prov-Spain-2 prov-Spain-5 prov-Spain-15 prov-Spain-18" />
        sea.getChildren("located").forEach(l -> {
            if (l.getAttributeValue("province").contains(provinceId)) {
                l.setAttribute("province", l.getAttributeValue("province").replace(provinceId, "").replace("  ", " "));
            }
        });
        return sea;
    }

    private void fixAreaToCataloniaFromProvince(Element spain, Element catalonia,Element province) {
        double catArea = Double.parseDouble(province.getChild("area").getValue());
        double spainArea = Double.parseDouble(spain.getAttributeValue("area"));
        catalonia.setAttribute("area", String.valueOf(catArea));
        spain.setAttribute("area", String.valueOf(spainArea - catArea));
    }

    private String replaceSpainInIdWithCatalonia(String id) {

        if (id.contains(provinceId)) {
            replaceMap.put(provinceId, "prov-Catalonia-1");
            return id.replace(provinceId, "prov-Catalonia-1");
        }
        if (id.contains("Spain")) {
            replaceMap.put(id, id);
            String newId = id.replace("Spain", "Catalonia");
            replaceMap.put(id, newId);
            return newId;
        }
        return id;
    }

    private void fixCountryPopulation(Element country, Element catalonia) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private void fixRootForProvinceIds(Element root, Element province) {
        
    }

    private void fixRootForCityIds(Element root, Element province) {
        throw new UnsupportedOperationException("Not implemented");
    }



    private void fixBorderLengths(List<Element> countries, Element catalonia) {
        // First handle Catalonia's borders
        for (Element cataloniaBorder : catalonia.getChildren("border")) {
            String carCode = cataloniaBorder.getAttributeValue("country");
            Element country = countries.stream()
                .filter(c -> c.getAttributeValue("car_code").equals(carCode))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Country not found"));

            // Add new border to the country
            Element newBorder = new Element("border")
                .setAttribute("country", catalonia.getAttributeValue("car_code"))
                .setAttribute("length", cataloniaBorder.getAttributeValue("length"));
            country.addContent(newBorder);
        }

        // Then handle Spain's borders separately
        Element spain = countries.stream()
            .filter(c -> c.getAttributeValue("car_code").equals("E"))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Spain not found"));

        // Handle Spain's borders with France and Andorra
        Element franceBorder = spain.getChildren("border").stream()
            .filter(b -> b.getAttributeValue("country").equals("F"))
            .findFirst()
            .orElse(null);
        if (franceBorder != null) {
            franceBorder.setAttribute("length", String.valueOf(623 - 300));
        }

        Element andorraBorder = spain.getChildren("border").stream()
            .filter(b -> b.getAttributeValue("country").equals("AND"))
            .findFirst()
            .orElse(null);
        if (andorraBorder != null) {
            andorraBorder.detach();
        }

        // Update other countries' borders with Spain
        for (Element country : countries) {
            if (country == spain || country == catalonia) continue;
            
            Element spainBorder = country.getChildren("border").stream()
                .filter(b -> b.getAttributeValue("country").equals("E"))
                .findFirst()
                .orElse(null);
                
            if (spainBorder != null) {
                Element cataloniaBorder = catalonia.getChildren("border").stream()
                    .filter(b -> b.getAttributeValue("country").equals(country.getAttributeValue("car_code")))
                    .findFirst()
                    .orElse(null);
                    
                if (cataloniaBorder != null) {
                    double length = Double.parseDouble(spainBorder.getAttributeValue("length"));
                    double lengthCatalonia = Double.parseDouble(cataloniaBorder.getAttributeValue("length"));
                    double newLength = length - lengthCatalonia;
                    
                    if (newLength <= 0) {
                        spainBorder.detach();
                    } else {
                        spainBorder.setAttribute("length", String.valueOf(newLength));
                    }
                }
            }
        }
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
            Element cataloniaCountry = readCataloniaIntoElement();
            Element cataloniaClone = cataloniaCountry.clone();
            fixBorderLengths(E34a.ROOT.getChildren("country"), cataloniaClone);
            spain.detach();
            Element catProvince = getProvince(spain);
            System.out.println(catProvince.getChild("name").getValue());
            spain.removeContent(catProvince);
            
            Element spainClone = spain.clone();

            catProvince.setAttribute("id", replaceSpainInIdWithCatalonia(catProvince.getAttributeValue("id")));
            catProvince.setAttribute("country", "CAT");
            catProvince.setAttribute("capital", "cty-Catalonia-Barcelona");
            fixAreaToCataloniaFromProvince(spainClone, cataloniaClone, catProvince);
            // fix the id of the cities
            catProvince.getChildren("city").forEach(city -> {
                city.setAttribute("id", replaceSpainInIdWithCatalonia(city.getAttributeValue("id")));
                city.setAttribute("country", "CAT");
                city.setAttribute("province", "prov-Catalonia-1");
            });


            cataloniaClone.addContent(catProvince);
            E34a.ROOT.addContent(cataloniaClone);
            E34a.ROOT.addContent(spainClone);

            Element riverGaronne = getRiver(E34a.ROOT, "river-Garonne");
            System.out.println(riverGaronne.getChild("name").getValue());
            mutateRiver(riverGaronne);

            Element riverEbro = getRiver(E34a.ROOT, "river-Ebro");
            System.out.println(riverEbro.getChild("name").getValue());
            mutateRiver(riverEbro);

            Element riverSegre = getRiver(E34a.ROOT, "river-Segre");
            System.out.println(riverSegre.getChild("name").getValue());
            mutateRiver(riverSegre);

            Element riverValira = getRiver(E34a.ROOT, "river-Valira");
            System.out.println(riverValira.getChild("name").getValue());
            mutateRiver(riverValira);

            Element mountainEstats = getMountain(E34a.ROOT, "mount-Estats");
            System.out.println(mountainEstats.getChild("name").getValue()); 
            mutateMountain(mountainEstats);

            Element mountainComapedrosa = getMountain(E34a.ROOT, "mount-Comapedrosa");
            System.out.println(mountainComapedrosa.getChild("name").getValue());    
            mutateMountain(mountainComapedrosa);

            Element mountainCroscat = getMountain(E34a.ROOT, "mount-Croscat");
            System.out.println(mountainCroscat.getChild("name").getValue());    
            mutateMountain(mountainCroscat);

            Element seaMediterranean = getSea(E34a.ROOT, "sea-Mittelmeer");
            System.out.println(seaMediterranean.getChild("name").getValue());
            mutateSea(seaMediterranean);


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
        String xmlString = xmlOutput.outputString(E34a.ROOT);
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            xmlString = xmlString;//.replace(entry.getKey(), entry.getValue());
        }
        writer.write(xmlString);
        writer.close();
    }

    @SneakyThrows
    public static void main(String[] args) {
        E34a ex = new E34a();
        ex.run();
    }
}