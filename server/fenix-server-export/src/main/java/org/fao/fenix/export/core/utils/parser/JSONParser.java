package org.fao.fenix.export.core.utils.parser;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

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

        JsonNode result = null;

        ObjectMapper mapper = new ObjectMapper();

        try {
            result =  mapper.readTree(parameter.getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        return result;

    }

    public static <T> T toObject(String content, Class<T> objectClass) throws Exception {
        return new ObjectMapper().readValue(content, objectClass);
    }

    public static <T> T toObject(InputStream content, Class<T> objectClass) throws Exception {
        return new ObjectMapper().readValue(content, objectClass);
    }



}
