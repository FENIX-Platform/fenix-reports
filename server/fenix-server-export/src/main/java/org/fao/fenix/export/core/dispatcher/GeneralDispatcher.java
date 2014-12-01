package org.fao.fenix.export.core.dispatcher;

import org.codehaus.jackson.JsonNode;
import org.fao.fenix.export.core.input.factory.InputFactory;
import org.fao.fenix.export.core.utils.parser.JSONParser;

/**
 * Created by fabrizio on 12/1/14.
 */
public class GeneralDispatcher {
    String input,output,data,metadata;

    InputFactory inputFactory;

    JSONParser jsonParser;

    public GeneralDispatcher(String input, String output, String data, String metadata){
        this.input = input;
        this.output = output;
        this.data = data;
        this.metadata = metadata;
    }


    public void init (){

        this.jsonParser = JSONParser.getInstance();

        JsonNode inputJSONNode = this.jsonParser.requestParamsToJSON(this.input);
        JsonNode outputJSONNode = this.jsonParser.requestParamsToJSON(this.input);
        JsonNode dataJSONNode = this.jsonParser.requestParamsToJSON(this.input);
        JsonNode metadataJSONNode = this.jsonParser.requestParamsToJSON(this.input);

        this.inputFactory = InputFactory.getInstance();


    }
}
