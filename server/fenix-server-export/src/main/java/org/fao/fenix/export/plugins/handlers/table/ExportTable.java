package org.fao.fenix.export.plugins.handlers.table;

import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.plugins.handlers.table.utils.DatatypeFormatter;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * Created by fabrizio on 12/4/14.
 */
public class ExportTable {

    private JsonNode dataNode, metadataNode, inputConfigNode, outputConfigNode;


    private DatatypeFormatter datatypeFormatter;

    public Workbook init(Input input, Output output, HttpServletResponse response) {

        dataNode = input.getDataNode();
        metadataNode = input.getMetadataNode();
        inputConfigNode = input.getInputNode();
        outputConfigNode = output.getOutput();

        datatypeFormatter = new DatatypeFormatter();


        return null;
    }

    private void createDataRightFormat() throws NoSuchMethodException {

        JsonNode dsdColumns = metadataNode.get("dsd").get("columns");
        JsonNode dataArray = dataNode.get("data");
        JsonNode outputColumnConfig =outputConfigNode.get("config").get("visualization").get("columns");

        Class formatterClass = datatypeFormatter.getClass();

        for(int i=0; i< dataArray.size(); i++){

            JsonNode tempArrayData = dataArray.get(i).get(""+i);

            for(int j=0; j< tempArrayData.size(); j++){

                JsonNode outputTempConfig = outputColumnConfig.get(j);
                String data = tempArrayData.get(j).asText();

                String dataType = dsdColumns.get(j).get("dataType").asText();
                String dataTypeMethod = dataType.substring(0, 1).toUpperCase() + dataType.substring(1);
                Method method = formatterClass.getMethod("getRight"+dataTypeMethod+"Format",null);


            }












       }





        String input = "label";
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        Method method = formatterClass.getMethod("getRight"+output+"Format",null);



    }
}
