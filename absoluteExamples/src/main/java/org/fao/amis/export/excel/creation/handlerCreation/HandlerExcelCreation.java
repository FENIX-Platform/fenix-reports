package org.fao.amis.export.excel.creation.handlerCreation;


import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fao.amis.export.data.configurations.dataCreator.DataCreator;
import org.fao.amis.export.data.daoValue.DaoForecastValue;
import org.fao.amis.export.data.forecast.Forecast;
import org.fao.amis.export.data.query.AMISQuery;
import org.fao.amis.export.data.utils.commodity.CommodityParser;
import org.fao.amis.export.excel.creation.creator.SheetCreator;
import org.fao.amis.export.excel.creation.utils.AmisExcelUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by fabrizio on 11/10/14.
 */
public class HandlerExcelCreation {

    private SheetCreator sheetCreator;

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(HandlerExcelCreation.class);


    public HandlerExcelCreation() {
        this.sheetCreator = new SheetCreator();
    }


    public XSSFWorkbook init(Forecast forecast, AMISQuery qvo, DataCreator dataModel) {


        // create the Excel file
        XSSFWorkbook workbook = new XSSFWorkbook();
        AmisExcelUtils.setCustomizedPalette(workbook);


        //Initialize font
        AmisExcelUtils.initializeHSSFFontStyles(workbook);

        int[] commodityList = forecast.getCommodityList();

        for (int commodity : commodityList) {

            String commodityString = "" + commodity;

            CommodityParser commParser = new CommodityParser();

            String commodityLabel = commParser.getCommodityLabel(commodityString);

            XSSFSheet sheet = workbook.createSheet(commodityLabel);

            int rowCounter = 0;

            rowCounter = this.sheetCreator.createSummary(rowCounter, sheet, workbook, dataModel, commodityLabel);

            rowCounter = this.sheetCreator.createSheetTitle(rowCounter,sheet,workbook);

            /*
                -------------------------    FoodBalance   -------------------------------------------------
             */

            LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> foodBalanceResults = forecast.getFoodBalanceResults().get(commodityString);

            rowCounter = this.sheetCreator.createHeadersGroup(rowCounter,sheet,workbook, foodBalanceResults, "foodBalance");

            // list of elements to show on the left
            HashMap< Integer, HashMap<Integer, String>> elements =  qvo.getFoodBalanceElements();

            // put on the excel the elements and the values
            rowCounter = this.sheetCreator.createDataTableGroup(rowCounter,sheet,workbook,elements.get(commodity), foodBalanceResults);

            rowCounter++;

              /*
                -------------------------    ITY RESULTS   -------------------------------------------------
             */

            LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> ityResults = forecast.getItyResults().get(commodityString);

            rowCounter = this.sheetCreator.createHeadersGroup(rowCounter,sheet,workbook, ityResults, "international");

            // list of elements to show on the left
            HashMap< Integer, HashMap<Integer, String>> elementsITY =  qvo.getItyElements();

            // put on the excel the elements and the values
            rowCounter = this.sheetCreator.createDataTableGroup(rowCounter,sheet,workbook,elementsITY.get(commodity), ityResults);

            rowCounter++;
              /*
                -------------------------    OTHERS   -------------------------------------------------
             */

            LinkedHashMap<String, LinkedHashMap<String, DaoForecastValue>> otherResults = forecast.getOtherResults().get(commodityString);

            rowCounter = this.sheetCreator.createHeadersGroup(rowCounter,sheet,workbook, otherResults, "others");

            // list of elements to show on the left
            HashMap< Integer, HashMap<Integer, String>> elementsOTH =  qvo.getOtherElements();

            // put on the excel the elements and the values
            rowCounter = this.sheetCreator.createDataTableGroup(rowCounter,sheet,workbook,elementsOTH.get(commodity), otherResults);

            sheet.createFreezePane(1,0);

        }

        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("workbook.xlsx"));
            workbook.write(out);
            out.close();

            LOGGER.debug("demoEXcel.xlsx written successfully on disk.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
    }

}




