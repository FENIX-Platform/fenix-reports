package org.fao.fenix.export.services;

import org.apache.log4j.Logger;
import org.fao.fenix.export.core.controller.GeneralController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/fenix/export")
public class ServletFenixExport extends HttpServlet {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(ServletFenixExport.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LOGGER.warn("after response header");

        String input = request.getParameter("input");
        String output = request.getParameter("output");
        String data = request.getParameter("data");
        String metadata = request.getParameter("metadata");
        GeneralController dispatcher = new GeneralController(input,output,data,metadata);
        try {
            dispatcher.init(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
