package org.fao.fenix.export.core.utils.parser;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by fabrizio on 12/1/14.
 */
public class JSONParser {

    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(JSONParser.class);

    private static JSONParser jsonParser;

    private JSONParser(){}

    public static JSONParser getInstance() {

        if (jsonParser == null)
        {
            jsonParser = new JSONParser();
        }

        return jsonParser;
    }

    public JsonNode requestParamsToJSON(String parameter) {

        LOGGER.warn("start request params to JSON");

        JsonNode result = null;

        ObjectMapper mapper = new ObjectMapper();

        try {
            result =  mapper.readTree(parameter.getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // TODO if I want to pass Array of Array as data
        if(result== null){

        }

        return result;

    }

}
