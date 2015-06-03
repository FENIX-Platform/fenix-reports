package org.fao.fenix.export.plugins.input.fmd.mediator;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fao.fenix.export.plugins.output.fmd.dto.FMDQuestions;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;


public class FMDClientMediator {

    public JsonNode getParsedMetadata  () throws Exception {
        String url = "http://fenix.fao.org/demo/fmd/tests/schema4pdf.json";
        JsonNode result = null;
        Response response = ClientBuilder.newBuilder().build().target(url).request().get();
        try {
            result =  new ObjectMapper().readValue(response.readEntity(String.class), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;


      /*  String url = "/home/fabrizio/Documents/GenericProjects/fenix-reports/server/fenix-server-export/test/fmd/simpleTest/meta.json";
        JsonNode result = null;

        try {
            result =  new ObjectMapper().readValue(new File(url), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;*/
    }


    public FMDQuestions getParsedData () {
        String url = "/home/fabrizio/Documents/GenericProjects/fenix-reports/server/fenix-server-export/test/fmd/simpleTest/data.json";
        FMDQuestions result = new FMDQuestions();

        try {
          result = new ObjectMapper().readValue(new File(url), FMDQuestions.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

/*
    private void trasformObjectToBean (Object unstructuredData, FMDQuestions dataWithStructure) {

        ArrayList<Object> tt = (ArrayList)unstructuredData;
        for(int i =0; i<tt.size(); i++) {
            switch (i){
                case ask1_1Position :
                    dataWithStructure.setAsk1_1((Collection<String>) tt.get(i));
                    break;
                case ask1_Position:
                    dataWithStructure.setAsk1((String) tt.get(i));
                    break;
            }
        }
    }*/


}
