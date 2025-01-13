package org.example;

import org.jdom2.output.Format;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;

public class E35 {
    public static void run() throws Exception {
        // Create transformer with our XSLT stylesheet
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(new File("src/main/resources/transform.xslt"));
        Transformer transformer = factory.newTransformer(xslt);
        
        // Omit XML declaration from transformer output
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        
        // Set up source
        Source input = new StreamSource(new File("mondial.xml"));

        // Create a StringWriter for the transformation
        StringWriter transformedContent = new StringWriter();
        transformer.transform(input, new StreamResult(transformedContent));

        // Write the final output with DTD reference
        FileWriter writer = new FileWriter("output_3_5.xml");
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<!DOCTYPE mondial SYSTEM \"mondial.dtd\">\n");
        writer.write(transformedContent.toString());
        writer.close();
    }
}
