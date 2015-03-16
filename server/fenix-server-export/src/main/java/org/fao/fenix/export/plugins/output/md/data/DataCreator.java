package org.fao.fenix.export.plugins.output.md.data;


import com.fasterxml.jackson.databind.JsonNode;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataCreator {

    private Map<String, Object> metaDataCleaned;

    private JsonNode mdsd;

    private final static String LANG = "EN";


    public void initDataFromMDSD(JsonNode mdsdNode, MeIdentification meIdentification) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        this.mdsd = mdsdNode;
        Iterator<Map.Entry<String,JsonNode>> properties = mdsdNode.get("properties").fields();


        this.metaDataCleaned = new HashMap<String, Object>();

        while (properties.hasNext()) {

            Map.Entry<String, JsonNode> mapDsdTmp = properties.next();
            String key = mapDsdTmp.getKey();
            String methodString = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
            Method method = MeIdentification.class.getMethod(methodString, null);
            Object returnedValue = method.invoke(meIdentification, null);

            System.out.println("prova!");

            if (returnedValue != null) {

                if (mapDsdTmp.getValue().get("type") != null && mapDsdTmp.getValue().get("type").asText().equals("string")) {
                    this.metaDataCleaned.put(key, returnedValue);
                } else {
                    this.metaDataCleaned.put(key, fillRecursive(mapDsdTmp.getValue().fields(),returnedValue));

                }
            }
        }
    }


    private Object fillRecursive(Iterator<Map.Entry<String, JsonNode>> fields, Object returnedValue) {

        HashMap<String,Object> tempMap = new HashMap<String,Object>();

        while (fields.hasNext()) {

            Map.Entry<String,JsonNode> mapFieldDSD = fields.next();
            String key = mapFieldDSD.getKey();

            // different from object reference

            switch (key){

                case "patternProperties":







                    break;

                case "$ref":

                    String[] path = mapFieldDSD.getValue().asText().substring(2).split("/");





                    break;

                case "type":

                    break;


            }
            if(key.equals("$ref")){

            }












        }



        return null;

    }


}







