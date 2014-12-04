package org.fao.fenix.export.plugins.handlers.olap;


import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.exportHandler.communication.HandlerAdapter;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.output.plugin.Output;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by fabrizio on 12/4/14.
 */
public class AdapterOlapPivot implements HandlerAdapter {

    private ExportOlapPivot olapPivot;

    private Workbook workbook;

    @Override
    public Workbook createExport(Input input, Output output, HttpServletResponse response) {
        olapPivot = new ExportOlapPivot();

        JsonNode dataNode = input.getDataNode();
        String data = dataNode.get("data").toString();
        String flags = dataNode.get("flags").toString();
        try {
            workbook= olapPivot.init(data, flags, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }
}
