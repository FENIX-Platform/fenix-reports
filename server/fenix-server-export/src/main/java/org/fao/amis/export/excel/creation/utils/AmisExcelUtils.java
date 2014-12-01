package org.fao.amis.export.excel.creation.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;

import java.util.Map;

/**
 * Created by fabrizio on 11/10/14.
 */
public class AmisExcelUtils {

    public static XSSFFont boldFont;
    public static XSSFFont italicFont;
    public static XSSFFont italicisedSmallFont;
    public static XSSFFont whiteFont;
    public static XSSFFont bigBoldFont;
    public static XSSFFont smallFont;
    public static XSSFFont boldSmallFont;

    public static XSSFColor BLUE_CUSTOMIZED;
    public static XSSFColor GREY_CUSTOMIZED;


    public static int createEmptyRow(int rowCounter, XSSFSheet sheet, XSSFWorkbook workbook){
        XSSFRow row = sheet.createRow(rowCounter++);
        row.createCell((short) 0).setCellValue("");
        row.createCell((short) 1).setCellValue("");

        return rowCounter;
    }


    public static void setCustomizedPalette(XSSFWorkbook workbook){

       BLUE_CUSTOMIZED = new XSSFColor((new java.awt.Color(59, 104, 189)));
       GREY_CUSTOMIZED = new XSSFColor((new java.awt.Color(224, 238, 191)));

    }

    public static XSSFCellStyle getRightAlignmentStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        return style;
    }

    public static XSSFCellStyle getLeftAlignmentStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(XSSFCellStyle.ALIGN_LEFT);
        return style;
    }

    public static XSSFCellStyle getRightAlignmentWithBordersStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = getBordersStyle(workbook, null);
        style.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        return style;
    }


    public static XSSFCellStyle getBasicCellStyle(XSSFWorkbook workbook){
        XSSFCellStyle cellStyle = workbook.createCellStyle();

        return cellStyle;
    }

    public static XSSFCellStyle getBlueCellStyle(XSSFWorkbook workbook){
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND );
        cellStyle.setFillForegroundColor(BLUE_CUSTOMIZED);
        getBordersStyle(workbook, cellStyle);

        cellStyle.setFont(whiteFont);

        cellStyle.setWrapText(true);

        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);

        return cellStyle;

    }

    public static XSSFCellStyle getGreyCellStyle(XSSFWorkbook workbook){
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND );
        cellStyle.setFillForegroundColor(GREY_CUSTOMIZED);
        getBordersStyle(workbook, cellStyle);

        return cellStyle;

    }


    public static XSSFCellStyle getBigBoldTextCellStyle(XSSFWorkbook workbook, XSSFCellStyle cellStyle){

        if(cellStyle==null){
            cellStyle = getBasicCellStyle(workbook);
        }

        cellStyle.setFont(bigBoldFont);

        return cellStyle;

    }

    public  XSSFCellStyle getSmallTextCellStyle(XSSFWorkbook workbook, XSSFCellStyle cellStyle, Boolean setBold){

        if(cellStyle==null){
            cellStyle = getBasicCellStyle(workbook);
        }

        if(setBold)
            cellStyle.setFont(boldSmallFont);
        else
            cellStyle.setFont(smallFont);


        return cellStyle;

    }


    public static XSSFCellStyle getBoldTextCellStyle(XSSFWorkbook workbook, XSSFCellStyle cellStyle){

        if(cellStyle==null){
            cellStyle = getBasicCellStyle(workbook);
        }

        cellStyle.setFont(boldFont);

        return cellStyle;

    }


    public static XSSFCellStyle getBordersStyle(XSSFWorkbook workbook, XSSFCellStyle cellStyle){
        if(cellStyle==null){
            cellStyle = getBasicCellStyle(workbook) ;
        }
        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLUE_GREY.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLUE_GREY.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.BLUE_GREY.getIndex());
        cellStyle.setBottomBorderColor(IndexedColors.BLUE_GREY.getIndex());

        return  cellStyle;
    }


    public static void initializeHSSFFontStyles(XSSFWorkbook workbook){
        boldFont = workbook.createFont();
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

        bigBoldFont = workbook.createFont();
        bigBoldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        bigBoldFont.setFontHeightInPoints((short) 11);

        whiteFont = workbook.createFont();
        whiteFont.setColor(HSSFColor.WHITE.index);

        italicFont = workbook.createFont();
        italicFont.setItalic(true);

        italicisedSmallFont = workbook.createFont();
        italicisedSmallFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        italicisedSmallFont.setFontHeightInPoints((short) 8);

        smallFont = workbook.createFont();
        smallFont.setFontHeightInPoints((short) 8);

        boldSmallFont = workbook.createFont();
        boldSmallFont.setFontHeightInPoints((short) 8);
        boldSmallFont.setBoldweight(Font.BOLDWEIGHT_BOLD);

    }


    public  int createNoAvailableDataTable(int rowCounter, XSSFSheet sheet, XSSFWorkbook workbook, String title){
        rowCounter = createEmptyRow(rowCounter, sheet, workbook);

        //Title Row
        rowCounter = createHeadingRow(rowCounter, sheet, workbook, title, null);

        XSSFRow row = sheet.createRow(rowCounter++);
        XSSFCell cell = row.createCell((short) 0);
        cell.setCellStyle(getLeftAlignmentStyle(workbook));
        cell.setCellValue("No Data Available");

        return rowCounter;
    }

    public  int createHeadingRow(int rowCounter, XSSFSheet sheet,  XSSFWorkbook workbook, String header, String headerValue){
        XSSFRow row = sheet.createRow(rowCounter++);
        // LOGGER.info("----------- createHeadingRow .... START ");

        if(header != null && headerValue==null){
            XSSFCell cell = row.createCell((short) 0);
            cell.setCellStyle(getBigBoldTextCellStyle(workbook, null));
            cell.setCellValue(header);

            row.createCell((short) 1).setCellValue("");
        }
        else {
            // LOGGER.info("----------- header  "+header);

            XSSFCell cell = row.createCell((short) 0);
            cell.setCellStyle(getRightAlignmentStyle(workbook));
            cell.setCellValue(header);

            cell = row.createCell((short) 1);
            cell.setCellStyle(getBoldTextCellStyle(workbook, null));
            cell.setCellValue(headerValue);
        }

        // LOGGER.info("----------- createHeadingRow .... END rowCounter =  "+rowCounter);

        return rowCounter;
    }
}
