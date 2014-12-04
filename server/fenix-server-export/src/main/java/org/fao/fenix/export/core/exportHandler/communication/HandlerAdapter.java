package org.fao.fenix.export.core.exportHandler.communication;

import org.apache.poi.ss.usermodel.Workbook;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.output.plugin.Output;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by fabrizio on 12/4/14.
 */
public interface HandlerAdapter {

    public Workbook createExport(Input input, Output output, HttpServletResponse response);
}
