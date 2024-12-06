import Trees.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

// For Setup: Add in IntelliJ project settings https://stackoverflow.com/a/43574427
// i.e. jakarta.xml.bind:jakarta.xml.bind-api:2.3.2
// and org.glassfish.jaxb:jaxb-runtime:2.3.2

public class TreeMain {
    public static void main(String[] args) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        unmarshaller.setProperty("com.sun.xml.bind.ObjectFactory", new MyObjectFactory());

        MyTree tree = (MyTree) unmarshaller.unmarshal(new File("./4_2.xml"));

        System.out.println("Expression");
        System.out.println(tree.expression());

        System.out.println("Evaluated:");
        System.out.println(tree.evaluate());

        System.out.println("HTML");
        System.out.println(tree.tree());
    }
}
