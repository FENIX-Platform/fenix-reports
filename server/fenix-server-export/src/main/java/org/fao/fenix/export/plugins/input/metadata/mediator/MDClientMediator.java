package org.fao.fenix.export.plugins.input.metadata.mediator;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;


public class MDClientMediator {

    private final String DEFAULT_URL = "http://faostat3.fao.org/d3s2/v2/msd/resources/metadata/uid/";

    public MeIdentification getMetadata  (Map<String, Object> config) throws Exception {

        MeIdentification me = null;
        String prefix_url = (config.get("metadata_url")!= null && !config.get("metadata_url").toString().equals(""))?
                 config.get("metadata_url").toString(): DEFAULT_URL ;
        String url =prefix_url + config.get("uid").toString()+"@?full=true";
        if(config.get("uid")!= null) {
            url = (config.get("version") == null) ? url.replace("@", "") : url.replace("@", "/version/" + config.get("version").toString());
        }else{
            throw  new Exception("Error on input configuration");
        }
        MeIdentification result=null;
        Response response = ClientBuilder.newBuilder().build().target(url).request().get();
            try {
                result =  new ObjectMapper().readValue(response.readEntity(String.class), MeIdentification.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

        return  result;
    }


    public JsonNode getParsedMDSD (String url) {
        JsonNode result = null;
        Response response = ClientBuilder.newBuilder().build().target(url).request().get();
        try {
            result =  new ObjectMapper().readValue(response.readEntity(String.class), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return result;
    }

}
