package org.fao.amis.export.excel.entryPoint;


import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fao.amis.export.data.Factory.DataFactory;
import org.fao.amis.export.data.configurations.dataCreator.DataCreator;
import org.fao.amis.export.data.daoValue.DaoForecastValue;
import org.fao.amis.export.data.forecast.Forecast;
import org.fao.amis.export.data.query.AMISQuery;
import org.fao.amis.export.excel.creation.handlerCreation.HandlerExcelCreation;
import org.omg.CORBA.DATA_CONVERSION;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * Created by fabrizio on 11/3/14.
 */
//import statements
    @WebServlet
public class WriteExcelDemo extends HttpServlet{

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(WriteExcelDemo.class);


    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

       Object[][] data;

       String[] tempDataString = request.getParameterValues("data");
       String season = request.getParameter("season");
       String dataSource = request.getParameter("datasource");
       String region = request.getParameter("region");

       String[] temp;

       data = new Object[tempDataString.length][];
       for(int i =0, length = tempDataString.length; i<length; i++){
           temp = tempDataString[i].split(",",-1);
           data[i] = new Object[temp.length];
           for(int j=0; j<temp.length; j++){
               data[i][j] = (Object)temp[j];
           }
       }



        DataFactory dataFactory = new DataFactory( data, season, dataSource,region );
        Forecast forecast = dataFactory.getForecastIstance();
        AMISQuery qvo = dataFactory.getAMISQueryIstance();
        DataCreator fakeCostructor = dataFactory.getDataCreatorIstance();


        LOGGER.debug("FoodBalance");
        LOGGER.debug(qvo.getFoodBalanceElements().toString());
        LOGGER.debug("International");
        LOGGER.debug(qvo.getItyElements().toString());

        LOGGER.debug("othersss");
        LOGGER.debug(qvo.getOtherElements());


        LOGGER.debug("forecasts: getFoodBalanceREsults");
        LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>>> map =
                forecast.getUnorderedMap();

        LOGGER.debug("Food balance Results");
        LOGGER.debug(forecast.getFoodBalanceResults().toString());


        LOGGER.debug("forecasts: getFoodBalanceREsults");

        HandlerExcelCreation excelController = new HandlerExcelCreation();
        XSSFWorkbook workbook = excelController.init(forecast,qvo,fakeCostructor);


        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + "amisExport.xlsx");
        response.setContentType("application/vnd.openxml");

        workbook.write(response.getOutputStream());
        response.getOutputStream().close();

    }

}