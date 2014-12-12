package org.fao.fenix.export.plugins.handlers.table;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.plugins.handlers.table.utils.DatatypeFormatter;
import org.fao.fenix.export.plugins.handlers.table.utils.ExcelStyle;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by fabrizio on 12/4/14.
 */
public class ExportTable {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ExportTable.class);

    private JsonNode dataNode, metadataNode, inputConfigNode, outputConfigNode;

    private String extension;

    private ExcelStyle excelStyle;

    private DatatypeFormatter datatypeFormatter;

    public Workbook init(Input input, Output output, HttpServletResponse response) {

        excelStyle = new ExcelStyle();
        Workbook wb = null;

        datatypeFormatter = new DatatypeFormatter();

        dataNode = input.getDataNode();
        metadataNode = input.getMetadataNode();


        inputConfigNode = input.getInputNode();
        outputConfigNode = output.getOutput();

        JsonNode dsdColumns = metadataNode.get("dsd").get("columns");
        JsonNode dataArray = dataNode.get("data");
        JsonNode outputColumnConfig = outputConfigNode.get("config").get("visualization").get("columns");

        List<List<Object>> dataList = new LinkedList<List<Object>>();

        createColumnTitles(dataList, dsdColumns);

        createDataRightFormat(dataList, dsdColumns, dataArray, outputColumnConfig);

        wb = createExcel(dataList,outputConfigNode);

        String configFileName = outputConfigNode.get("config").get("fileName").asText();

        String fileName = (!configFileName.equals("null") && !configFileName.equals(""))? configFileName: "fenixExport";

        fileName = fileName+"."+extension;

        if(wb instanceof HSSFWorkbook){
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(fileName));
                wb.write(out);
                out.close();
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename="+fileName);
                wb.write(response.getOutputStream());
                response.getOutputStream().close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(wb instanceof XSSFWorkbook){
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(fileName));
                wb.write(out);
                out.close();
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=" +fileName);
                response.setContentType("application/vnd.openxml");
                wb.write(response.getOutputStream());
                response.getOutputStream().close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return wb;
    }

    private void createColumnTitles(List<List<Object>> dataList, JsonNode dsdColumns) {

        List<Object> tempList = null;
        tempList = new LinkedList<>();

        for (int i = 0; i < dsdColumns.size(); i++) {

            tempList.add(dsdColumns.get(i).get("title").get("EN").asText());
        }
        dataList.add(tempList);
        LOGGER.warn(dataList);

    }

    private void createDataRightFormat(List<List<Object>> dataList, JsonNode dsdColumns, JsonNode dataArray, JsonNode outputColumnConfig) {


        Class formatterClass = datatypeFormatter.getClass();

        for (int i = 0; i < dataArray.size(); i++) {

            JsonNode tempArrayData = dataArray.get(i).get("" + i);
            List<Object> tempList = new LinkedList<>();

            for (int j = 0; j < tempArrayData.size(); j++) {

                JsonNode outputTempConfig = outputColumnConfig.get(j);
                String data = tempArrayData.get(j).asText();

                String dataType = dsdColumns.get(j).get("dataType").asText();
                String dataTypeMethod = dataType.substring(0, 1).toUpperCase() + dataType.substring(1);
                Method method = null;

                try {

                    Class[] paramString = new Class[]{dsdColumns.get(j).getClass(), String.class, outputTempConfig.getClass()};
                    method = formatterClass.getMethod("getRight" + dataTypeMethod + "Format", paramString);
                    Object resultMethod = method.invoke(datatypeFormatter, dsdColumns.get(j), data, outputTempConfig);

                    tempList.add(resultMethod);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

            }

            dataList.add(tempList);

        }

        LOGGER.warn(dataList.toString());
    }

    private Workbook createExcel( List<List<Object>> dataList, JsonNode outputConfigNode) {

        Workbook wb;

        String title = outputConfigNode.get("config").get("title").asText();
        extension = outputConfigNode.get("config").get("extension").asText();
        String sheetName = outputConfigNode.get("config").get("sheetName").asText();

        wb = (extension.equals("xls")) ? new HSSFWorkbook() : new XSSFWorkbook();

        Map<String, CellStyle> styles = excelStyle.createStyles(wb);

        // TO DO:
        Sheet sheet = (!sheetName.equals(null) && !sheetName.equals("")) ? wb.createSheet(sheetName) : wb.createSheet("Sheet");

        int rowCounter = 0;
        int startColumn = 0;

        /* TITLE */
        Row headerRow = sheet.createRow(rowCounter++);

        headerRow.setHeightInPoints(26.75f);

        Cell titleCell = headerRow.createCell(startColumn);
        titleCell.setCellValue(title);

        rowCounter++;


        for (int i = 0; i < dataList.size(); i++) {
            List<Object> list = dataList.get(i);
            Row row = sheet.createRow(rowCounter++);

            for (int j = 0; j < list.size(); j++) {

                /* HEADER COLUMNS */

                if (i == 0) {

                    Cell cell = row.createCell(j);
                    cell.setCellValue(list.get(j).toString());
                    cell.setCellStyle(styles.get("header"));
                    sheet.autoSizeColumn(rowCounter);

                } else {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(list.get(j).toString());
                    sheet.autoSizeColumn(rowCounter);

                }
            }
        }


        /* DATE */
        rowCounter = rowCounter+2;
        Row row = sheet.createRow(rowCounter);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        String dateHour = dateFormat.format(new Date());
        row.createCell(0).setCellValue("Date :");
        row.createCell(1).setCellValue(dateHour);

        return wb;

    }

}
