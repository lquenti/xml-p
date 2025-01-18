package org.semwebtech.servletdemo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class SumServlet extends HttpServlet {

	private static final long serialVersionUID = 2846766312757038782L;

	private static final Logger logger = Logger.getLogger(SumServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

         	resp.setContentType("text/html");
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

		logger.info("returning result " + result);
 
		PrintWriter out = resp.getWriter();

		out.print(result);
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// this only works because HTTPServlet already handles the parsing of
		// content-type "application/x-www-form-urlencoded"!

		doGet(req, resp);
	}
}
