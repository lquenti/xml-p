import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ex4_5{
    public static String getMountainXMLFragment(String mountainName) {
        try {
            String url = "https://en.wikipedia.org/wiki/" + mountainName.replace(" ", "_");
            Document doc = Jsoup.connect(url).get();

            String height = "";
            String location = "";

            Element infobox = doc.selectFirst("table.infobox"); // Select the Wikipedia infobox
            if (infobox != null) {
                for (Element row : infobox.select("tr")) {
                    String header = row.selectFirst("th") != null ? row.selectFirst("th").text() : "";
                    String value = row.selectFirst("td") != null ? row.selectFirst("td").text() : "";

                    if (header.contains("Elevation")) {
                        height = value.replaceAll("\\[.*?\\]", "").trim(); // Remove references like [1]
                    } else if (header.contains("Location")) {
                        location = value.replaceAll("\\[.*?\\]", "").trim();
                    }
                }
            }

            // Create XML fragment
            org.jdom2.Element mountainElement = new org.jdom2.Element("mountain");
            mountainElement.setAttribute("name", mountainName);

            org.jdom2.Element heightElement = new org.jdom2.Element("height");
            heightElement.setText(height);
            mountainElement.addContent(heightElement);

            org.jdom2.Element locationElement = new org.jdom2.Element("location");
            locationElement.setText(location);
            mountainElement.addContent(locationElement);

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            return outputter.outputString(mountainElement);

        } catch (IOException e) {
            e.printStackTrace();
            return "<error>Could not fetch data for " + mountainName + "</error>";
        }
    }

    public static List<String> getMountainsInGermany(String mondialFilePath) {
        List<String> mountainsInGermany = new ArrayList<>();
        try {
            File file = new File(mondialFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList mountainList = doc.getElementsByTagName("mountain");
            for (int i = 0; i < mountainList.getLength(); i++) {
                org.w3c.dom.Element mountain = (org.w3c.dom.Element) mountainList.item(i);
                String countries = mountain.getAttribute("country");
                List<String> countryList = Arrays.asList(countries.split("\\s* \\s*"));
                if (countryList.contains("D")) { // 'D' is Germany's car code
                    String mountainName = mountain.getElementsByTagName("name").item(0).getTextContent();
                    mountainsInGermany.add(mountainName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mountainsInGermany;
    }

    public static void main(String[] args) {
        // Get all mountains located in Germany
        List<String> germanMountains = getMountainsInGermany("mondial.xml");
        System.out.println("Mountains in Germany: " + germanMountains);

        // Fetch mountain data from Wikipedia and generate XML fragments
        for (String mountainName : germanMountains) {
            System.out.println("Fetching data for: " + mountainName);
            String mountainXML = getMountainXMLFragment(mountainName);
            System.out.println(mountainXML);
        }
    }
}
