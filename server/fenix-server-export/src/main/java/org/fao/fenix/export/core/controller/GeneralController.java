package org.fao.fenix.export.core.controller;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.exportHandler.communication.HandlerAdapter;
import org.fao.fenix.export.core.exportHandler.factory.HandlerFactory;
import org.fao.fenix.export.core.input.factory.InputFactory;
import org.fao.fenix.export.core.output.factory.OutputFactory;
import org.fao.fenix.export.core.utils.parser.JSONParser;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by fabrizio on 12/1/14.
 */
public class GeneralController {

    private String input, output, data, metadata;

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(GeneralController.class);

    private InputFactory inputFactory;
    private OutputFactory outputFactory;

    private HandlerFactory handlerFactory;
    private HandlerAdapter handlerExport;

    private Workbook workbook;

    private JSONParser jsonParser;

    public GeneralController(String input, String output, String data, String metadata) {
        this.input = input;
        this.output = output;
        this.data = data;
        this.metadata = metadata;
    }


    public Workbook init(HttpServletResponse response) {
        LOGGER.warn("Genreal controller.init");

        this.jsonParser = JSONParser.getInstance();

        JsonNode inputJSONNode = this.jsonParser.requestParamsToJSON(this.input);
        JsonNode outputJSONNode = this.jsonParser.requestParamsToJSON(this.output);
        JsonNode dataJSONNode = this.jsonParser.requestParamsToJSON(this.data);
        JsonNode metadataJSONNode = this.jsonParser.requestParamsToJSON(this.metadata);
/*
        this.inputFactory = InputFactory.getInstance();
        Input inputChosen = inputFactory.init(inputJSONNode, dataJSONNode, metadataJSONNode);

        this.outputFactory = OutputFactory.getInstance();
        Output outputChosen = outputFactory.init(outputJSONNode);

        this.handlerFactory = HandlerFactory.getInstance();
        handlerExport = this.handlerFactory.init(inputChosen);

        workbook = handlerExport.createExport(inputChosen,outputChosen,response);
*/
        return workbook;

    }
}
