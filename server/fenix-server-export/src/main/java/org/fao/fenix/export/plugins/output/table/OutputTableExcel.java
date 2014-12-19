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
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.plugins.output.table.utilsMetadata.DatatypeFormatter;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

public class OutputTableExcel extends Output {

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
        coreOutputHeader.setName(((config.get("fileName") != null)? config.get("fileName").toString(): "fenixExport.xlsx"));
        coreOutputHeader.setSize(100);
        coreOutputHeader.setType(CoreOutputType.xlsx);
        return coreOutputHeader;
    }

    @Override
    public void write(OutputStream outputStream) throws Exception {
        wb.write(outputStream);
        outputStream.close();
        wb.dispose();
    }



    private SXSSFWorkbook createExcel(Collection<DSDColumn> collection, Iterator<Object[]> data) throws Exception {

        formatterValue = new DatatypeFormatter();
        formatterValue.init(config.get("lang"));

        SXSSFWorkbook wb = new SXSSFWorkbook(100);
        String sheetName  =((String)config.get("sheetName"));

        Sheet sh = ( sheetName != null && sheetName!= "")? wb.createSheet(sheetName) : wb.createSheet();
        int rowCounter = 0;
        ArrayList collectionDSD = (ArrayList)collection;
        rowCounter = createHeaders(sh,collectionDSD,rowCounter, config.get("lang"));
        rowCounter = createBody(sh,collectionDSD,data ,rowCounter);
        createFooters(sh,rowCounter);

        return wb;
    }

    private int createHeaders(Sheet sheet, ArrayList<DSDColumn> columns ,int rowCounter, Object lang){

        // title
        Row rowTitle = sheet.createRow(rowCounter);
        rowTitle.setHeightInPoints(26.75f);
        rowTitle.createCell(0).setCellValue((config.get("title")!=null)? config.get("title").toString().toUpperCase(): "FENIX EXPORT");
        rowCounter +=3;

        // column headers
        Row rowColumnHeaders = sheet.createRow(rowCounter);

        for(int i=0; i<columns.size(); i++) {
            Map<String, String> titles = columns.get(i).getTitle();
            if (titles != null) {

                if (titles.get(lang) == null) {
                    boolean notFound = true;
                    Iterator itTitles = titles.keySet().iterator();

                    while (itTitles.hasNext() && notFound) {
                        String titleKeyLabel = itTitles.next().toString();
                        if (titles.get(titleKeyLabel) != null) {
                            rowColumnHeaders.createCell(i).setCellValue(titles.get(titleKeyLabel).toUpperCase());
                            notFound =false;
                        }
                    }
                } else {
                    rowColumnHeaders.createCell(i + 1).setCellValue(titles.get("EN").toUpperCase());
                }
            }
        }
        return rowCounter;

    }

    private int createBody(Sheet sheet,ArrayList<DSDColumn> columns, Iterator<Object[]> data, int rowCounter) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Class formatterClass = formatterValue.getClass();

        ArrayList listConfigColumns = ((ArrayList)(config.get("columns")));

        Map<Integer,Map<String, Class[]>> mapMethods = createMethodsSet(columns);

        // row
        while (data.hasNext()){
            Object[] rowData = (Object[])data.next();
            rowCounter++;
            Row row = sheet.createRow(rowCounter);

            for(int i =0; i< rowData.length; i++){

                String methodName = mapMethods.get(i).keySet().iterator().next();
                Method method = formatterClass.getMethod(methodName,mapMethods.get(i).get(methodName));

                Object resultMethod = method.invoke(formatterValue, columns.get(i), rowData[i].toString(), ((LinkedHashMap)listConfigColumns.get(i)));
                row.createCell(i).setCellValue(resultMethod.toString());
            }
        }

        return rowCounter;
    }

    private Map<Integer,Map<String, Class[]>> createMethodsSet( ArrayList<DSDColumn> columnList){

        Map<Integer,Map<String, Class[]>> result= new HashMap<Integer,Map<String, Class[]>>();
        Class formatterClass = formatterValue.getClass();

        Map<String,Class[]> methodsMap = new HashMap<String,Class[]>();
        Method[] methods =  formatterValue.getClass().getMethods();

        for(int i=0;i< methods.length ; i++)
            methodsMap.put(methods[i].getName(),methods[i].getParameterTypes());

        for(int i=0; i<columnList.size(); i++){
            Map<String, Class[]> tempMap = new HashMap<>();
            String dataType = columnList.get(i).getDataType().toString();
            String dataTypeMethod = "getRight"+dataType.substring(0, 1).toUpperCase() + dataType.substring(1)+"Format";
            tempMap.put(dataTypeMethod,methodsMap.get(dataTypeMethod));
            result.put((Integer)i,tempMap);
        }


        return result;
    }


    private void createFooters(Sheet sheet, int rowCounter){

        rowCounter +=3;
        Row rowFooter = sheet.createRow(rowCounter);
        rowFooter.createCell(0).setCellValue("Created on : ");
        rowFooter.createCell(1).setCellValue(new SimpleDateFormat("dd/MMM/yy").format(new Date()));
    }

}
