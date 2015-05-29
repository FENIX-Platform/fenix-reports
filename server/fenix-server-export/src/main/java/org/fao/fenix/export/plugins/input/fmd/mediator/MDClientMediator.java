package org.fao.fenix.export.plugins.input.fmd.mediator;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.export.plugins.output.fmd.dto.FMDQuestions;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;


public class MDClientMediator {


    public MeIdentification getMetadata  (Map<String, Object> config) throws Exception {

        MeIdentification me = null;
        String url = "http://faostat3.fao.org/d3s2/v2/msd/resources/metadata/uid/"+config.get("uid").toString()+"@?full=true";
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


    public FMDQuestions getParsedMDSD () {
        String url = "/home/fabrizio/Documents/GenericProjects/fenix-reports/server/fenix-server-export/test/fmd/simpleTest/data.json";
        FMDQuestions result = null;
        Response response = ClientBuilder.newBuilder().build().target(url).request().get();
        try {
            result =  new ObjectMapper().readValue(response.readEntity(String.class), FMDQuestions.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        JsonNode result = null;
        try {
            result = new ObjectMapper().readValue(new File("/home/fabrizio/Documents/GenericProjects/fenix-reports/server/fenix-server-export/jsonExamples/Config/guideMDSD2.json"), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        return result;
    }

}
