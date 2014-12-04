package org.fao.fenix.export.plugins.handlers.table;

import org.apache.poi.ss.usermodel.Workbook;
import org.fao.fenix.export.core.exportHandler.communication.HandlerAdapter;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.output.plugin.Output;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by fabrizio on 12/4/14.
 */
public class AdapterTable implements HandlerAdapter {

    private ExportTable exportTable;

    private Workbook workbook;

    @Override
    public Workbook createExport(Input input, Output output, HttpServletResponse response) {

        exportTable = new ExportTable();

        workbook= exportTable.init(input, output, response);

        return workbook;
    }
}
