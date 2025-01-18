package de.lquenti.reverseserver;

import java.io.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

@WebServlet(name = "reverseServlet", value = "/reverse")
public class ReverseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/plain");
        res.getWriter().write("use post");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            res.setContentType("application/xml");
            res.setCharacterEncoding("UTF-8");

            // I read that bufferedreader should be used for non-binary input
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(res.getOutputStream()));

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            ReverseXMLHandler handler = new ReverseXMLHandler(out);

            saxParser.parse(req.getInputStream(), handler);
        } catch (SAXException | ParserConfigurationException e) {
            System.err.println("doPost: " + e.getMessage());
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}