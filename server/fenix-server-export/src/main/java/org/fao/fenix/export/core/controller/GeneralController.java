package org.fao.fenix.export.core.controller;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.input.factory.InputFactory;
import org.fao.fenix.export.core.input.plugin.Input;
import org.fao.fenix.export.core.output.factory.OutputFactory;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.core.utils.parser.JSONParser;
import org.fao.fenix.export.plugins.olapPivot.OlapPivot;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by fabrizio on 12/1/14.
 */
public class GeneralController {

    private String input,output,data,metadata;

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(GeneralController.class);

    private InputFactory inputFactory;

    private OutputFactory outputFactory;

    private JSONParser jsonParser;

    public GeneralController(String input, String output, String data, String metadata){
        this.input = input;
        this.output = output;
        this.data = data;
        this.metadata = metadata;
    }


    public void init (HttpServletResponse response){
        LOGGER.warn("Genreal controller.init");

        this.jsonParser = JSONParser.getInstance();

        JsonNode inputJSONNode = this.jsonParser.requestParamsToJSON(this.input);
        JsonNode outputJSONNode = this.jsonParser.requestParamsToJSON(this.output);
        JsonNode dataJSONNode = this.jsonParser.requestParamsToJSON(this.data);
        JsonNode metadataJSONNode = this.jsonParser.requestParamsToJSON(this.metadata);

        this.inputFactory = InputFactory.getInstance();
        Input inputChosen = inputFactory.init(inputJSONNode, dataJSONNode, metadataJSONNode);
        this.outputFactory = OutputFactory.getInstance();
        Output outputChosen = outputFactory.init(outputJSONNode);

        if(inputChosen.getInputName() == "inputOlap"){
            try {
                OlapPivot olapPivot = new OlapPivot();
                String data = dataJSONNode.get("data").asText();
                String flags = dataJSONNode.get("flags").asText();
                olapPivot.init(data,flags);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
