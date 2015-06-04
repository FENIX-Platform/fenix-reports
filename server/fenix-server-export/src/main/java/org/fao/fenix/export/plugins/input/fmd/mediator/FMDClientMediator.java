package org.fao.fenix.export.plugins.input.fmd.mediator;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fao.fenix.export.plugins.output.fmd.dto.FMDQuestions;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;


public class FMDClientMediator {

    private final String URLDATA_FIRST_PART = "http://fenixapps2.fao.org/wds_5.1/rest/crud?payload=%7B%22query%22%3A%7B%22_id%22%3A%7B%22%24oid%22%3A%22";
    private final String URLDATA_APPEND_PART = "%22%7D%7D%0A%7D&datasource=FMD&collection=survey&outputType=object";


    public JsonNode getParsedMetadata  () throws Exception {


        String url = "https://raw.githubusercontent.com/FENIX-Platform-Projects/fmd-ui/master/tests/schema4pdf.json";
/*
        String url = "http://fenix.fao.org/demo/fmd/tests/schema4pdf.json";
*/
        JsonNode result = null;
        Response response = ClientBuilder.newBuilder().build().target(url).request().get();
        try {
            result =  new ObjectMapper().readValue(response.readEntity(String.class), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }


    public FMDQuestions getParsedData (String uid) {

        FMDQuestions result = new FMDQuestions();

        if (uid != null) {
            String url = URLDATA_FIRST_PART + uid + URLDATA_APPEND_PART;

            Response response = ClientBuilder.newBuilder().build().target(url).request().get();
            try {
                result = new ObjectMapper().readValue(response.readEntity(String.class), FMDQuestions[].class)[0];
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}
