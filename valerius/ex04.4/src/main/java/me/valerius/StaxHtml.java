package me.valerius;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class StaxHtml {
    public static void parseAndTransform(String inputFile, String outputFile) {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

            inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

            Reader fileReader = Files.newBufferedReader(Paths.get(inputFile));
            XMLStreamReader reader = inputFactory.createXMLStreamReader(fileReader);

            Writer fileWriter = Files.newBufferedWriter(Paths.get(outputFile));
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(fileWriter);

            writer.writeStartDocument("UTF-8", "1.0");

            // Process the document
            boolean skipContent = false;
            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String elementName = reader.getLocalName().toLowerCase();
                    if (elementName.equals("style") || elementName.equals("script")) {
                        skipContent = true;
                        continue;
                    }

                    if (!skipContent) {
                        writer.writeStartElement(reader.getLocalName());
                        // Copy attributes
                        for (int i = 0; i < reader.getAttributeCount(); i++) {
                            writer.writeAttribute(
                                    reader.getAttributeLocalName(i),
                                    reader.getAttributeValue(i));
                        }
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    String elementName = reader.getLocalName().toLowerCase();
                    if (elementName.equals("style") || elementName.equals("script")) {
                        skipContent = false;
                        continue;
                    }

                    if (!skipContent) {
                        writer.writeEndElement();
                    }
                } else if (event == XMLStreamConstants.CHARACTERS && !skipContent) {
                    writer.writeCharacters(reader.getText());
                } else if (event == XMLStreamConstants.CDATA && !skipContent) {
                    writer.writeCData(reader.getText());
                }
            }

            // Close everything
            writer.writeEndDocument();
            writer.close();
            reader.close();
            fileWriter.close();
            fileReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        parseAndTransform("montblanc.html", "montblanc_stax.html");
    }
}
