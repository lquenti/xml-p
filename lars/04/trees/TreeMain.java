import Trees.ObjectFactory;
import Trees.Tree;
import Trees.Op;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
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

        Tree tree = (Tree) unmarshaller.unmarshal(new File("./4_2.xml"));
        Op sub = tree.getSub();
        System.out.println(sub.getAddOrSubOrMul().get(0).getDeclaredType().getSimpleName());
        Op mul = (Op) sub.getAddOrSubOrMul().get(0).getValue();
        Op add = (Op) mul.getAddOrSubOrMul().get(0).getValue();
        System.out.println(add.getAddOrSubOrMul().get(0).getDeclaredType().getSimpleName());
        long val = (long) add.getAddOrSubOrMul().get(0).getValue();
        System.out.println(val);
    }
}
