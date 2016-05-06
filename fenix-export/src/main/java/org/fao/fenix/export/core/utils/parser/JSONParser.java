package org.fao.fenix.export.core.utils.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.export.core.dto.CoreConfig;
import org.fao.fenix.export.core.dto.PluginConfig;
import org.fao.fenix.export.core.utils.adapter.FenixAdapter;

import java.io.IOException;
import java.io.InputStream;


public class JSONParser {

    private static ObjectMapper jacksonMapper;


    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(JSONParser.class);

    private static JSONParser jsonParser;

    private static FenixAdapter fenixAdapter;

    private JSONParser() {
    }

    public static JSONParser getInstance() {

        if (jsonParser == null) {
            jsonParser = new JSONParser();
        }

        return jsonParser;
    }

    public JsonNode requestParamsToJSON(String parameter) {

        JsonNode result = null;

        ObjectMapper mapper = new ObjectMapper();

        try {
            result = mapper.readTree(parameter.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;

    }

    public <T> T toObject(String content, Class<T> objectClass) throws Exception {
        return new ObjectMapper().readValue(content, objectClass);
    }

    public <T> T toObject(InputStream content, Class<T> objectClass) throws Exception {
        return new ObjectMapper().readValue(content, objectClass);
    }


    public static CoreConfig createFenixCore(InputStream content) throws Exception {
        createOrGetUtils();
        JsonNode payload = jacksonMapper.readTree(content);
        Resource resource = fenixAdapter.decodeResource(payload.get("resource").toString(), fenixAdapter.getRepresentationType("metadata", payload.get("resource")));
        return new CoreConfig( jacksonMapper.treeToValue(payload.get("input"), PluginConfig.class),  jacksonMapper.treeToValue(payload.get("output"), PluginConfig.class), resource);
    }


    private static void createOrGetUtils () {
        if(jsonParser ==null && jacksonMapper == null) {
            fenixAdapter = new FenixAdapter();
            jacksonMapper = new ObjectMapper();
        }
    }

}
