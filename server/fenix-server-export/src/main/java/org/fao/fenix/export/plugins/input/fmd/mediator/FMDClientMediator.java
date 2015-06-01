package org.fao.fenix.export.plugins.input.fmd.mediator;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fao.fenix.export.plugins.output.fmd.dto.FMDQuestions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class FMDClientMediator {

    private final int ask1_1Position = 1;
    private final int ask1_Position = 0;


    public JsonNode getParsedMetadata  () throws Exception {
        String url = "/home/fabrizio/Documents/GenericProjects/fenix-reports/server/fenix-server-export/test/fmd/simpleTest/meta.json";
        JsonNode result = null;

        try {
            result =  new ObjectMapper().readValue(new File(url), JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public FMDQuestions getParsedData () {
        String url = "/home/fabrizio/Documents/GenericProjects/fenix-reports/server/fenix-server-export/test/fmd/simpleTest/data.json";
        FMDQuestions result = new FMDQuestions();

        try {
          trasformObjectToBean(new ObjectMapper().readValue(new File(url), Object.class),result );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


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
    }


}
