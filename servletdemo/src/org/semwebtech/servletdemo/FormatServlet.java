package org.semwebtech.servletdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class FormatServlet extends HttpServlet {

    private static final long serialVersionUID = -8644396054056558046L;

    private static final Logger logger = Logger.getLogger(FormatServlet.class);

    /**
     * The collection "results" stores all generated HTML result tables. To
     * avoid ConcurrentModificationExceptions, the collection is synchronized
     * upon creation.
     */
    private static Collection<String> results =
	Collections.synchronizedCollection(new ArrayList<String>());


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {
	// doGet: here, three different calls come in, both from the Web form:
	//  /reset:  reset the results collection,
	//  /all:    print "all result tables",
	//  /format: with parameters a,b to output a formatted table
	// => they must be distinguished by the called URL path tail:

	// the following outputs (going to catalina.out) demonstrate the
	// possibilities to analyze the URL-path/string used in the call:

	// depending how web.xml specifies the assignments, getPathInfo()
	// *or* getServletPath() is null. Together, they contain
	// the relevant information.
	System.out.println("getPathInfo(): " + req.getPathInfo());
	System.out.println("getServletPath(): " + req.getServletPath());

	String path = req.getPathInfo();
	if (path == null)
	    path = req.getServletPath();

	System.out.println(path);
	if (path.startsWith("/reset")) {
	    results = Collections.synchronizedCollection(new ArrayList<String>());
	}
	else if (path.startsWith("/format")) {
	    String strA = req.getParameter("a");
	    String strB = req.getParameter("b");
	    logger.info("parameters - a: " + strA + " , b: " + strB);
	    double a, b, result;

	    try {
		a = Double.parseDouble(strA);
		b = Double.parseDouble(strB);
	    } catch (Exception e) {
		logger.fatal("could not parse arguments", e);
		resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
		return;
	    }

	    result = a + b;
	    // here: response is the response to the HTTP call from the HTML form/browser
	    createResponse(strA, strB, Double.toString(result), resp);
	}
	else if (path.startsWith("/all")) {
    
	    PrintWriter out = resp.getWriter();
	    out.print(createDocumentHeader("Generated Result Tables"));
	    out.print("<h1>Generated Result Tables:</h1>\n");
	    for (String resultTable : results) {
		out.print(resultTable);
		out.print("\n<br/>\n");
	    }
	    out.print(createDocumentFooter());
	    out.flush();
	    out.close();
	}
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

	// POST: here, the request from the MakeCallsServlet comes in
	//    it contains an XML message with a, b, and result in the body
	BufferedReader in = req.getReader();
	SAXBuilder builder = new SAXBuilder();
	Document doc;

	// build the document from the incoming POST request
	try {
	    doc = builder.build(in);
	} catch (JDOMException e) {
	    logger.error("could not build DOM from request", e);
	    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	    return;
	}

	Element root = doc.getRootElement();

	// get the parameters
	String a = root.getChildTextTrim("a");
	String b = root.getChildTextTrim("b");
	String result = root.getChildTextTrim("result");

	if (a == null || b == null || result == null) {
	    logger.error("xml not valid");
	    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "xml not valid");
	    return;
	}
	    // here: response is the response to HTTP call from the MakeCallServlet
	createResponse(a, b, result, resp);
    }

    private static void createResponse(String a, String b,
                                       String result, HttpServletResponse resp) 
	throws IOException {
	// note: resp is a parameter to which (wherever it then goes) the result
	// is written

	String resultTable = createResultTable(a, b, result);
	PrintWriter out = resp.getWriter();
	out.print(resultTable);
	out.flush();
	out.close();
	results.add(resultTable);
    }

    private static final String createResultTable(String a, String b,
						  String result) {
	StringBuilder sb = new StringBuilder();

	sb.append("<table border=\"1\">\n");
	sb.append("  <tr><th>a</th><th>b</th><th>result (a + b)</th></tr>\n");
	sb.append("  <tr>\n");
	sb.append("    <td>").append(a).append("</td>\n");
	sb.append("    <td>").append(b).append("</td>\n");
	sb.append("    <td>").append(result).append("</td>\n");
	sb.append("  </tr>\n");
	sb.append("</table>");

	return sb.toString();
    }

    private String createDocumentHeader(String title) {
	StringBuilder sb = new StringBuilder();
	sb.append("<!DOCTYPE HTML PUBLIC ").append(
						   "\"-//W3C//DTD HTML 4.0 Transitional//EN\">\n");
	sb.append("<html>\n<head><title>").append(title).append(
								"</title></head>\n").append("<body>\n");

	return sb.toString();
    }

    private String createDocumentFooter() {
	return "</body></html>\n";
    }
}
