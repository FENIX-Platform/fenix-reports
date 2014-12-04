package org.fao.fenix.export.plugins.handlers.olap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fabrizio on 12/1/14.
 */
public class ExportOlapPivot {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(ExportOlapPivot.class);


    public Workbook init(String data, String flags, HttpServletResponse response) throws IOException {
        String patternStr = "<span class=\"ordre\">.*</span><table class=\"innerCol\"><th>(.*)</th><th>(.*)</th></table>";
        Pattern pattern = Pattern.compile(patternStr);


        // Input
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("sheet1");
  
        /*BEGIN JACKSON*/

        ObjectMapper mapper = new ObjectMapper();

        JsonNode nodeData = mapper.readTree(new String(data.getBytes(), Charset.forName("UTF-8")).replaceAll("\\?", ""));

        ObjectMapper mapperFlag = new ObjectMapper();

        JsonNode nodeFlags = mapperFlag.readTree(new String(flags.getBytes(), Charset.forName("UTF-8")).replaceAll("\\?", ""));

        int i = 0;
        String head[];
        String Oldhead[] = null;
        Iterator<Map.Entry<String, JsonNode>> nodeIterator = nodeData.get("data").getFields();
        String swUnit = nodeData.get("swUnit").asText();
        String swFlag = nodeData.get("swFlag").asText();
        int swflagindex = 1;
        if (swUnit.equals("true")) {
            swflagindex = 2;
        }

        while (nodeIterator.hasNext()) {

            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodeIterator.next();

            head = entry.getKey().split("\\|\\|");

            if (i == 0) {
                HSSFRow row = sheet.createRow(0);
                int iii = 0;


                for (final JsonNode objNode : nodeData.get("cols")) {
                    row.createCell((short) iii).setCellValue(objNode.asText());
                    iii++;
                }


                for (final JsonNode objNode : nodeData.get("header")) {


                    String[] tpheadcell = objNode.asText().split("\\|\\|");
                    String retHeadTmp = "";
                    for (String k : tpheadcell) {
                        Matcher matcher = pattern.matcher(k);
                        if (matcher.matches()) {
                            retHeadTmp += " " + matcher.group(2) + "(" + matcher.group(1) + ")";
                        } else {
                            retHeadTmp += " " + k.replaceAll("<span class=\"ordre\">.*</span>", "");
                        }
                    }
                    row.createCell((short) iii).setCellValue(retHeadTmp);
                    iii++;
                    if (swUnit.equals("true")) {
                        row.createCell((short) iii).setCellValue("unit");
                        iii++;
                    }
                    if (swFlag.equals("true")) {
                        row.createCell((short) iii).setCellValue("flag");
                        iii++;
                    }
                }

            }


            HSSFRow row = sheet.createRow(i + 1);
            boolean stop = true;
            int j = 0;
            int jj = 0;
            for (String k : head) {

                String ret1 = k.replaceAll("<span class=\"ordre\">.*</span>", "");
                Matcher matcher = pattern.matcher(k);
                try {
                    if (stop
                            && i > 0
                            && Oldhead[j].replaceAll("<span class=\"ordre\">.*</span>", "").equals(ret1)) {

                        sheet.addMergedRegion(new CellRangeAddress(i, i + 1, jj, jj));
                        if (matcher.find()) {

                            jj++;
                            sheet.addMergedRegion(new CellRangeAddress(i, i + 1, jj, jj));
                        }


                    } else {


                        if (matcher.find()) {

                            row.createCell((short) jj).setCellValue(matcher.group(1));
                            jj++;
                            row.createCell((short) jj).setCellValue(matcher.group(2));
                        } else {

                            row.createCell((short) jj).setCellValue(ret1);
                        }
                        stop = false;

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                j++;
                jj++;
            }

            Oldhead = head;


            for (final JsonNode objNode : nodeData.get("header")) {
                try {
                    //  entry.getValue();
                    row.createCell((short) jj).setCellValue(Double.parseDouble(entry.getValue().get(objNode.asText()).get("sum").get(0).toString()));
                    jj++;
                    if (swUnit.equals("true")) {
                        row.createCell((short) jj).setCellValue(entry.getValue().get(objNode.asText()).get("sum").get(1).toString().replaceAll("&nbsp;", "").replaceAll("\"", ""));
                        jj++;
                    }
                    if (swFlag.equals("true")) {
                        row.createCell((short) jj).setCellValue(entry.getValue().get(objNode.asText()).get("sum").get(swflagindex).toString().replaceAll("&nbsp;", "").replaceAll("\"", ""));
                        jj++;
                    }
                } catch (Exception e) {
                    row.createCell((short) jj).setCellValue(" ");
                    jj++;
                    if (swUnit.equals("true")) {
                        row.createCell((short) jj).setCellValue(" ");
                        jj++;
                    }
                    if (swFlag.equals("true")) {
                        row.createCell((short) jj).setCellValue(" ");
                        jj++;
                    }
                }
            }
            i++;
        }


        HSSFRow row = sheet.createRow(++i);

        for (final JsonNode objNode : nodeFlags.get("data")) {
            row = sheet.createRow(i++);
            row.createCell((short) 0).setCellValue(objNode.get("title").asText());
            row.createCell((short) 1).setCellValue(objNode.get("label").asText());
        }

        row = sheet.createRow(i++);

        row = sheet.createRow(i++);
        // Title

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MMM/dd");
        String dateHour = dateFormat.format(new Date());
        row.createCell((short) 0).setCellValue("Date :");
        row.createCell((short) 1).setCellValue(dateHour);

        LOGGER.warn("new!");

        FileOutputStream out = new FileOutputStream(new File("fenixExport.xls"));
        wb.write(out);
        out.close();

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=fenixExport.xls");

        wb.write(response.getOutputStream());
        response.getOutputStream().close();

        return wb;
    }



}
