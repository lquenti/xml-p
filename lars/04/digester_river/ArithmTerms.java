import java.io.File;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;

public class ArithmTerms {
  public static void main(String[] args) {
    File term = new File("arithm-tree-example.xml");
    final Digester digester = new Digester();
    //  Rule: push values as Integers on the stack
    digester.addRule("*/val", new Rule(){
     public void body(String namespace, String name, String text) throws Exception {
        digester.push(new Integer(Integer.parseInt(text)));
        System.out.println("value:" + text );
      }
    } );
    //  Rule: operators: combine the two top stack values accordingly:
    digester.addRule("*/plus", new Rule(){
      public void end(String namespace, String name) throws Exception {
        int n = (Integer)(digester.pop()) + (Integer)(digester.pop());
        digester.push(new Integer(n));
        System.out.println("plus: " + n); }
      } );


    digester.addRule("*/minus", new Rule(){
      public void end(String namespace, String name) throws Exception {
        int min = (Integer)(digester.pop());
        int n = (Integer)(digester.pop()) - min;
        digester.push(new Integer(n));
        System.out.println("minus: " + n ); }
      } );
    digester.addRule("*/mult", new Rule(){
      public void end(String namespace, String name) throws Exception {
        int n = (Integer)(digester.pop()) * (Integer)(digester.pop());
        digester.push(new Integer(n));
        System.out.println("mult: " + n); }
    } );
    digester.addRule("*/div", new Rule(){
      public void end(String namespace, String name) throws Exception {
        Integer div = (Integer)(digester.pop());
        int n = (Integer)(digester.pop()) / div;
        digester.push(new Integer(n));
        System.out.println("div: " + n); }
    } );
    try { digester.setValidating(false);
          Integer result = digester.parse(term);
	  System.out.println("result: " + result);
      } catch (Exception e) { e.printStackTrace(); }   } }
