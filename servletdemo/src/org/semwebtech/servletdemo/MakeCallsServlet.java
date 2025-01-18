package org.semwebtech.servletdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Example Servlet for the XML Lab
 */

public class MakeCallsServlet extends HttpServlet {

	/**
	 * serialVersionUID is needed by Java's Serialization API for determining
	 * the version of a class. A ServletContainer (e.g. Tomcat) may choose to
	 * serialize the current state of its servlet(s) while e.g. shutting down or
	 * doing load balancing.
	 */
	private static final long serialVersionUID = 4909973689899568398L;
    
	/**
	 * log4j-logger
	 *
	 * configuration of the logger is done via log4j.properties file in the
	 * WEB-INF/classes directory of the servlet.
	 */
	private static final Logger logger = Logger.getLogger(MakeCallsServlet.class);

        private static String myURL = "";
	/**
	 * Servlet initialization sequence: - Class Constructor - init()
	 */
    public MakeCallsServlet() {
	super();
	logger.info("servlet constructor");
    }

    @Override
    public void init(ServletConfig cfg) throws ServletException {
	// use an init parameter from the web.xml
	// myURL is needed to communicate with the other servlets
	myURL = cfg.getInitParameter("myURL");
	logger.info("servlet init ... " + myURL);
    }

    // note: this servlet does not override the doGet(...) method, since nobody would call it.
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException {

	// the basic idea is that the servlet gets two parameters, a and b,
	// which are forwarded via a HTTP-GET request to the SumServlet. The
	// result is then sent alongside a and b in the form of a custom XML
	// document to the FormatServlet via an HTTP-POST request. The
	// FormatServlet generates an html table which is embedded in the output
	// of this servlet.

	logger.info("doPost invoked");

	String a = req.getParameter("a");
	String b = req.getParameter("b");

	logger.info("parameters - a: " + a + " , b: " + b);

	String result = callSum(a, b);

	if (result == null) {
	    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			   "could not reach sum servlet");
	}

	String resultTable = callFormat(a, b, result);

	if (resultTable == null) {
	    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
			   "could not reach format servlet");
	}

	StringBuilder sb = new StringBuilder();
	sb.append(createDocumentHeader("ExampleServlet Result"));
	sb.append("<b>MakeCallsServlet of ServletDemo</b> running at " + myURL + ":<br/>");
	sb.append("raw input parameters: <br/>\n");
	sb.append("a: ").append(a).append("<br/>\n");
	sb.append("b: ").append(b).append("<br/>\n");
	sb.append("<br/>raw result of ./sum: <br/>\n");
	sb.append("result: ").append(result).append("<br/>\n");
	sb.append("<br/>raw result of ./format:<pre>");
	sb.append(resultTable.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
	sb.append("</pre>\n");
	sb.append("<br/>formatted table: <br/>\n");
	sb.append(resultTable);

	sb.append(createDocumentFooter());

	PrintWriter out = resp.getWriter();

	out.print(sb.toString());
	out.flush();
	out.close();

	logger.info("...finished");
    }

    private static final String callSum(String a, String b) {

	logger.info("calling sum servlet");

	try {
	    String queryString = "a=" + URLEncoder.encode(a, "UTF-8") + "&b="
		+ URLEncoder.encode(b, "UTF-8");

	    // calling the SumServlet via http get
	    URL url = new URL(myURL + "/sum?" + queryString);
	    HttpURLConnection.setFollowRedirects(true);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();

            Object o = url.getContent();
	    System.out.println("I got a " + o.getClass().getName());
	    //  it just gets a sun.net.www.protocol.http.HttpURLConnection$HttpInputStream =:(
	    
	    // Setting some connection parameters
	    con.setRequestMethod("GET");
	    con.setDoInput(true);
	    con.setUseCaches(false);
	    con.connect();
	    logger.info("sum servlet answered with status "
			+ con.getResponseCode());
	    if (con.getResponseCode() != HttpServletResponse.SC_OK) {
		return con.getResponseMessage();
	    }
	    BufferedReader in =
	       new BufferedReader(new InputStreamReader(con.getInputStream()));

	    logger.info("reading response");
	    StringBuilder sb = new StringBuilder();
	    String line;
	    while ((line = in.readLine()) != null) {
		if (sb.length() > 0)
		    sb.append('\n');

		sb.append(line);
	    }
	    logger.info("sum servlet response: " + sb.toString());

	    in.close();
	    return sb.toString();

	} catch (Exception e) {
	    logger.warn("exception during call of sum servlet", e);
	    return null;
	}
}

    private String callFormat(String a, String b, String result) {

	logger.info("calling format servlet");

	try {
	    String requestXML = createFormatRequest(a, b, result);

	    logger.info("sending request:\n" + requestXML);

	    byte[] requestBytes = requestXML.getBytes("UTF-8");

	    // calling the FormatServlet via http post
	    URL url = new URL(myURL + "/format");
	    HttpURLConnection.setFollowRedirects(true);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();

	    // Setting some connection parameters
	    con.setRequestMethod("POST");
	    con.setDoInput(true);
	    con.setDoOutput(true);
	    con.setUseCaches(false);
	    con.setRequestProperty("Content-Type", "text/html");
	    con.connect();
	    OutputStream out = con.getOutputStream();
	    out.write(requestBytes);
	    out.flush();
	    out.close();


	    logger.info("format servlet answered with status "
			+ con.getResponseCode());

	    if (con.getResponseCode() != HttpServletResponse.SC_OK) {
		return con.getResponseMessage();
	    }

	    BufferedReader in =
	        new BufferedReader(new InputStreamReader(con.getInputStream()));

	    logger.info("reading response");
	    StringBuilder sb = new StringBuilder();
	    String line;
	    while ((line = in.readLine()) != null) {
		if (sb.length() > 0)
		    sb.append('\n');

		sb.append(line);
	    }

	    in.close();

	    return sb.toString().trim();

	} catch (Exception e) {
	    logger.warn("exception during call of format servlet", e);
	    return null;
	}
    }

    private static final String createFormatRequest(String a, String b,
						    String result) {
	Element request = new Element("format").setAttribute("type", "sum");
	Document doc = new Document(request);

	request.addContent(new Element("a").setText(a));
	request.addContent(new Element("b").setText(b));
	request.addContent(new Element("result").setText(result));

	return documentToString(doc);
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

    private static final String documentToString(Document doc) {
	XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
	return out.outputString(doc).trim();
    }
}
