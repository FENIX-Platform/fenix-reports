package org.fao.fenix.export.plugins.output.table;


import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output2;
import org.fao.fenix.export.plugins.output.table.utilsMetadata.DatatypeFormatter;

import java.io.OutputStream;
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
        return null; //TODO
    }

    @Override
    public void write(OutputStream outputStream) throws Exception {
        wb.write(outputStream);
        outputStream.close();
        wb.dispose();
    }



    private SXSSFWorkbook createExcel(Collection<DSDColumn> collection, Iterator<Object[]> data) throws Exception {

        SXSSFWorkbook wb = new SXSSFWorkbook(100);
        String sheetName  =((String)config.get("sheetName"));
        formatterValue = new DatatypeFormatter();
        formatterValue.init(config.get("lang"));

        Sheet sh = ( sheetName != null && sheetName!= "")? wb.createSheet(sheetName) : wb.createSheet();
        int rowCounter = 0;
        rowCounter = createHeaders(sh,(ArrayList)collection,rowCounter, config.get("lang"));
        //TODO
/*        for(int rownum = 0; rownum < 1000; rownum++){
            Row row = sh.createRow(rownum);
            for(int cellnum = 0; cellnum < 10; cellnum++){
                Cell cell = row.createCell(cellnum);
                String address = new CellReference(cell).formatAsString();
                cell.setCellValue(address);
            }

        }

        // Rows with rownum < 900 are flushed and not accessible
        for(int rownum = 0; rownum < 900; rownum++){
            if (sh.getRow(rownum)!=null)
                System.out.println("errore alla riga: "+rownum);
        }

        // ther last 100 rows are still in memory
        for(int rownum = 900; rownum < 1000; rownum++){
            if (sh.getRow(rownum)==null)
                System.out.println("errore alla riga: "+rownum);
        }
*/
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

    private int createBody(Sheet sheet,ArrayList<DSDColumn> columns, Iterator<Object> data, int rowCounter) throws NoSuchMethodException {

        Class formatterClass = formatterValue.getClass();

        // row
        while (data.hasNext()){
            Object[] rowData = (Object[])data.next();
            rowCounter++;
            Row row = sheet.createRow(rowCounter);


            for(int i =0; i< rowData.length; i++){

                String dataType = columns.get(i).getDataType().toString();
                String dataTypeMethod = dataType.substring(0, 1).toUpperCase() + dataType.substring(1);


                Class[] paramString =new Class[]{columns.get(i).getClass(), String.class, LinkedHashMap.class};
                Method method = formatterClass.getMethod("getRight" + dataTypeMethod + "Format", paramString);
        //        Object resultMethod = method.invoke(formatterValue, columns.get(i), data, (config.get("columns"));

             //   row.createCell(i).setCellValue(formatterValue);








            }
        }



        return 0;
    }

    private void createFooters(){
    //TODO
    }

}
