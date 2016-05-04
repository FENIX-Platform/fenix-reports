package org.fao.fenix.export.core.utils.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.DSDDocument;
import org.fao.fenix.commons.msd.dto.full.DSDGeographic;
import org.fao.fenix.commons.msd.dto.templates.identification.DSDCodelist;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.commons.utils.JSONUtils;
import org.fao.fenix.export.core.dto.CoreConfig;
import org.fao.fenix.export.core.dto.PluginConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class JSONParser {

    private static ObjectMapper jacksonMapper;


    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(JSONParser.class);

    private static JSONParser jsonParser;

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
        jacksonMapper = new ObjectMapper();
        JsonNode payload = jacksonMapper.readTree(content);
        PluginConfig inputPlugin = jacksonMapper.treeToValue(payload.get("input"), PluginConfig.class);
        PluginConfig outputPlugin = jacksonMapper.treeToValue(payload.get("output"), PluginConfig.class);
        Resource resource = decodeResource(payload.get("resource").toString(), getRepresentationType("metadata", payload.get("resource")));
        return new CoreConfig(inputPlugin, outputPlugin, resource);
    }

/*
    public static String readContent(InputStream inputStream) {
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }*/

    public  static RepresentationType getRepresentationType(String metadataField, JsonNode resourceNode) throws Exception {
        JsonNode metadataNode = resourceNode != null && resourceNode.get(metadataField) != null ? resourceNode.get(metadataField) : null;
        return getRepresentationType(metadataNode);
    }

    public static RepresentationType getRepresentationType(JsonNode metadataNode) throws Exception {


        String representationTypeLabel = metadataNode != null &&  metadataNode.get("meContent")!= null &&  metadataNode.get("meContent").get("resourceRepresentationType")!= null ? metadataNode.path("meContent").path("resourceRepresentationType").textValue() : null;
        return representationTypeLabel != null ? RepresentationType.valueOf(representationTypeLabel) : RepresentationType.dataset;
    }

    public static Resource decodeResource(String source, RepresentationType resourceType) throws Exception {
        switch (resourceType) {
            case codelist:
                return JSONUtils.decode(source, Resource.class, DSDCodelist.class, Code.class);
            case dataset:
                return JSONUtils.decode(source, Resource.class, DSDDataset.class, Object[].class);
            case geographic:
                return JSONUtils.decode(source, Resource.class, DSDGeographic.class, Object.class);
            case document:
                return JSONUtils.decode(source, Resource.class, DSDDocument.class, Object.class);
            default:
                return null;
        }
    }
}
