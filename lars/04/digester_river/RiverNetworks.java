import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RiverNetworks {
    public static void main(String[] args) throws IOException, SAXException {
        File mondial = new File("../../../mondial.xml");
        final Digester digester = new Digester();
        digester.push(new WaterCollection());
        WaterCollection w;

        /*
         * Documentation for myself:
         *
         * CallMethodRule
         * `digester.addCallMethod(String pattern, String methodName, int paramCount)`
         * Rule implementation that calls a method on an object on the stack (normally the top/parent object),
         * passing arguments collected from subsequent CallParamRule rules or from the body of this element.
         *
         * CallParamRule
         * `digester.addCallParam(String pattern, int paramIndex)`
         * Rule implementation that saves a parameter for use by a surrounding CallMethodRule.
         *
         * SetNextRule
         * `digester.addSetNext(String pattern, String methodName)`
         * Rule implementation that calls a method on the (top-1) (parent) object, passing the top object (child) as
         * an argument.
         * It is commonly used to establish parent-child relationships.
         *
         * SetPropertiesRule
         * `digester.addSetProperty(String pattern, String attributeName, String propertyName)`
         * Rule implementation that sets properties on the object at the top of the stack, based on attributes with
         * corresponding names.
         *
         *
         */

        // Sea
        digester.addObjectCreate("mondial/sea", Sea.class);
        digester.addSetProperties("mondial/sea", "id", "id");
        digester.addCallMethod("mondial/sea/name", "setName", 1);
        digester.addCallParam("mondial/sea/name", 0);
        digester.addSetNext("mondial/sea", "addSea");

        // Lake
        digester.addObjectCreate("mondial/lake", Lake.class);
        digester.addSetProperties("mondial/lake", "id", "id");
        digester.addCallMethod("mondial/lake/name", "setName", 1);
        digester.addCallParam("mondial/lake/name", 0);
        digester.addSetProperties("mondial/lake/to", "water", "into_id");
        digester.addSetNext("mondial/lake", "addLake");

        // River
        digester.addObjectCreate("mondial/river", River.class);
        digester.addSetProperties("mondial/river", "id", "id");
        digester.addCallMethod("mondial/river/name", "setName", 1);
        digester.addCallParam("mondial/river/name", 0);
        digester.addCallMethod("mondial/river/length", "setLength", 1);
        digester.addCallParam("mondial/river/length", 0);
        digester.addSetProperties("mondial/river/to", "water", "into_id");

        digester.addSetNext("mondial/river", "addRiver");


        digester.setValidating(false);
        w = digester.parse(mondial);
        WaterGraph g = new WaterGraph(w.waters);
        float len = g.getNetworkOfWater("river-Rhein");
        System.out.println(len);
    }
}