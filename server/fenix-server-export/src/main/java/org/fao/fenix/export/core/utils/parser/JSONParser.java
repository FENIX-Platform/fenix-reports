package org.fao.fenix.export.core.utils.parser;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * Created by fabrizio on 12/1/14.
 */
public class JSONParser {

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


        JsonNode result = null;

        ObjectMapper mapper = new ObjectMapper();

        try {
            result =  mapper.readTree(parameter.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

}
