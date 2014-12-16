package org.fao.fenix.export.services;

import org.apache.log4j.Logger;
import org.fao.fenix.export.core.controller.GeneralController2;
import org.fao.fenix.export.core.dto.CoreConfig;
import org.fao.fenix.export.core.utils.parser.JSONParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/fenix/export2")
public class ServletFenixExport2 extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ServletFenixExport2.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LOGGER.warn("after response header");

        CoreConfig config = null;
        try {
            config = JSONParser.toObject(request.getInputStream(), CoreConfig.class);
        } catch (Exception e) {
            throw new ServletException("Configuration parsing exception.", e);
        }

        GeneralController2 core = null;
        try {
            core = new GeneralController2(config);
        } catch (Exception e) {
            throw new ServletException("Initialization exception.", e);
        }

        try {
            //Create header
      //      CoreOutputHeader outputHeader = core.getHeader();
      //      response.setHeader("file", outputHeader.getName());
      //      response.setHeader("Content-Type", outputHeader.getType().getContentType());
      //      response.setHeader("Size", String.valueOf(outputHeader.getSize()));
            //Produce output
            core.write(response.getOutputStream());
        } catch (Exception e) {
            throw new ServletException("Output data producing error.", e);
        }
    }


}
