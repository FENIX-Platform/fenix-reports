package org.fao.fenix.export.core;

import org.apache.log4j.Logger;
import org.fao.fenix.export.core.dispatcher.GeneralDispatcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet
public class ServletFenixExport  {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(ServletFenixExport.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String input = request.getParameter("plugins/input");
        String output = request.getParameter("output");
        String data = request.getParameter("data");
        String metadata = request.getParameter("metadata");
        GeneralDispatcher dispatcher = new GeneralDispatcher(input,output,data,metadata);
        dispatcher.init();





    }


}
