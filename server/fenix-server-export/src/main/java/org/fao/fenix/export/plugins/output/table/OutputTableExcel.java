package org.fao.fenix.export.plugins.output.table;


import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output2;
import org.fao.fenix.export.plugins.output.table.utilsMetadata.DatatypeFormatter;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class OutputTableExcel extends Output2 {

    private static final Logger LOGGER = Logger.getLogger(OutputTableExcel.class);

    private Map<String, Object> config;
    private CoreData resource;
    SXSSFWorkbook wb;
    DatatypeFormatter formatterValue;

    @Override
    public void init(Map<String, Object> config) {
        this.config = config;
    }

    @Override
    public void process(CoreData resource) throws Exception {
        this.resource = resource;
        wb = createExcel(((DSDDataset)resource.getMetadata().getDsd()).getColumns(), resource.getData());
    }

    @Override
    public CoreOutputHeader getHeader() throws Exception {

        CoreOutputHeader coreOutputHeader = new CoreOutputHeader();
        coreOutputHeader.setName("provaName");
        coreOutputHeader.setSize(100);
        coreOutputHeader.setType(CoreOutputType.xlsx);
        return coreOutputHeader; //TODO
    }

    @Override
    public void write(OutputStream outputStream) throws Exception {
        wb.write(outputStream);
   //     outputStream.close();
        wb.dispose();
    }



    private SXSSFWorkbook createExcel(Collection<DSDColumn> collection, Iterator<Object[]> data) throws Exception {

        formatterValue = new DatatypeFormatter();
        formatterValue.init(config.get("lang"));

        SXSSFWorkbook wb = new SXSSFWorkbook(100);
        String sheetName  =((String)config.get("sheetName"));

        Sheet sh = ( sheetName != null && sheetName!= "")? wb.createSheet(sheetName) : wb.createSheet();
        int rowCounter = 0;
        rowCounter = createHeaders(sh,(ArrayList)collection,rowCounter, config.get("lang"));
        rowCounter = createBody(sh,(ArrayList)collection,data ,rowCounter);

        return wb;
    }

    private int createHeaders(Sheet sheet, ArrayList<DSDColumn> columns ,int rowCounter, Object lang){

        for(int i=0; i<columns.size(); i++) {
            Map<String, String> titles = columns.get(i).getTitle();
            if (titles != null) {

                if (titles.get(lang) == null) {
                    for (String key : titles.keySet()) {
                        if (titles.get(key) != null) {
                            sheet.createRow(rowCounter).createCell(i + 1).setCellValue(titles.get(key));
                        }
                    }
                } else {
                    sheet.createRow(rowCounter).createCell(i + 1).setCellValue(titles.get("EN"));
                }
                rowCounter++;
            }
        }
        return rowCounter;

    }

    private int createBody(Sheet sheet,ArrayList<DSDColumn> columns, Iterator<Object[]> data, int rowCounter) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class formatterClass = formatterValue.getClass();

        // row
        while (data.hasNext()){
            Object[] rowData = (Object[])data.next();
            rowCounter++;
            Row row = sheet.createRow(rowCounter);


            ArrayList listColumns = ((ArrayList)(config.get("columns")));
            for(int i =0; i< rowData.length; i++){

                String dataType = columns.get(i).getDataType().toString();
                String dataTypeMethod = dataType.substring(0, 1).toUpperCase() + dataType.substring(1);


                Class[] paramString =new Class[]{columns.get(i).getClass(), String.class, LinkedHashMap.class};
                Method method = formatterClass.getMethod("getRight" + dataTypeMethod + "Format", paramString);
                LOGGER.warn(formatterValue.getClass().toString());
                LOGGER.warn(data.getClass().toString());
                LOGGER.warn( ((LinkedHashMap)listColumns.get(i)).getClass().toString());

                Object resultMethod = method.invoke(formatterValue, columns.get(i), rowData[i].toString(), ((LinkedHashMap)listColumns.get(i)));
                row.createCell(i).setCellValue(resultMethod.toString());
            }
        }



        return rowCounter;
    }

    private void createFooters(){
    //TODO
    }

}
