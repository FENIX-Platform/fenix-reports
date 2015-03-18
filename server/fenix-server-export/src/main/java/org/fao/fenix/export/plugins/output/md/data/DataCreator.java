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
    private final static String TITLE_FIELD = "title_i18n";
    private final static String REF_FIELD = "$ref";
    private final static String TYPE_FIELD = "type";
    private final static String PATTERN_PROPERTIES_FIELD = "patternProperties";
    private final static String FOLLOW_PATTERN_PROPERTIES = ".{1,}";
    private final static String ENUM_FIELD = "enum";
    private final static String PROPERTIES_FIELD = "properties";
    private final static String STRING_TYPE = "string";
    private final static String DATE_TIME_TYPE = "date-time";
    private final static String ARRAY_TYPE = "array";
    private final static String OBJECT_TYPE = "object";


    public void initDataFromMDSD(JsonNode mdsdNode, MeIdentification meIdentification) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        this.mdsd = mdsdNode;
        Iterator<Map.Entry<String, JsonNode>> properties = mdsdNode.get(PROPERTIES_FIELD).fields();

        this.metaDataCleaned = new HashMap<String, Object>();

        while (properties.hasNext()) {

            Map.Entry<String, JsonNode> mapDsdTmp = properties.next();
            String key = mapDsdTmp.getKey();

            Object returnedValue = invokeMethodByReflection(key, meIdentification, false);

            /*
            String methodString = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
            Method method = MeIdentification.class.getMethod(methodString, null);
            Object returnedValue = method.invoke(meIdentification, null);*/

            if (returnedValue != null) {
                JsonNode tempField = mapDsdTmp.getValue().get(TITLE_FIELD);
                if (tempField != null && tempField.get(LANG.toLowerCase()) != null) {
                    if (mapDsdTmp.getValue().get(TYPE_FIELD) != null && mapDsdTmp.getValue().get(TYPE_FIELD).asText().equals(STRING_TYPE)) {
                        this.metaDataCleaned.put(tempField.get(LANG.toLowerCase()).asText(), returnedValue);
                    } else {
                        if (mapDsdTmp.getValue().get(REF_FIELD) != null) {
                            JsonNode msdRef = getMdsdObjectFromReference(mapDsdTmp.getValue().get(REF_FIELD).asText());
                            this.metaDataCleaned.put(tempField.get(LANG.toLowerCase()).asText(), fillRecursive(msdRef.fields(), returnedValue));
                        }
                    }
                }
            }
        }
        System.out.println("finish!");

    }


    private Object fillRecursive(Iterator<Map.Entry<String, JsonNode>> fields, Object returnedValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        HashMap<String, Object> tempMap = new HashMap<String, Object>();


        while (fields.hasNext()) {

            Map.Entry<String, JsonNode> mapFieldDSD = fields.next();
            String key = mapFieldDSD.getKey();

            // different from object reference

            switch (key) {

                case PATTERN_PROPERTIES_FIELD:

                    tempMap.put(LANG, ((Map) (returnedValue)).get(LANG));

                    break;

                case REF_FIELD:

                    String[] path = mapFieldDSD.getValue().asText().substring(2).split("/");

                    String url = null;
                    JsonNode res = null;
                    JsonNode refMdsd = mdsd;
                    for (int i = 0; i < path.length; i++) {
                        res = refMdsd.get(path[i]);
                        refMdsd = res;
                    }
                    tempMap.put(mapFieldDSD.getValue().asText(), fillRecursive(mapFieldDSD.getValue().fields(), returnedValue));

                    break;

                case TYPE_FIELD:


                    // TODO: add type 'MAP' as standard
                    switch (mapFieldDSD.getValue().asText()) {

                        case STRING_TYPE:
                        case DATE_TIME_TYPE:

                            tempMap.put(LANG, returnedValue);

                            break;

                        case ARRAY_TYPE:
                            /*
                             * Type Array can have as sibling a items field
                             */

                            System.out.println("array");
                            break;

                        case OBJECT_TYPE:
                            /*
                             * Type Object can have as sibling a properties field or patternProperties field
                             */

                            if (fields.hasNext()) {
                                mapFieldDSD = fields.next();
                            } else {
                                break;
                            }

                            String keyObj = mapFieldDSD.getKey();
                            String titleObj = (mapFieldDSD.getValue().get(TITLE_FIELD) != null) ? mapFieldDSD.getValue().get(TITLE_FIELD).get(LANG.toLowerCase()).asText() :
                                    "objectTitleProvv" + 10000 * Math.random() + "";
                            if (keyObj.equals(PROPERTIES_FIELD)) {

                                Iterator<Map.Entry<String, JsonNode>> tempIt = mapFieldDSD.getValue().fields();
                                while(tempIt.hasNext()){
                                    Map.Entry<String, JsonNode> objectValue = tempIt.next();
                                    Object msdValue = invokeMethodByReflection(objectValue.getKey(),returnedValue, false);
                                    if(msdValue!= null)
                                        tempMap.put(objectValue.getKey(),fillRecursive(objectValue.getValue().fields(), msdValue));
                                }

                            } else if (keyObj.equals(REF_FIELD)) {

                                
                            } else if(keyObj.equals(PATTERN_PROPERTIES_FIELD)){

                                if(mapFieldDSD.getValue().get(FOLLOW_PATTERN_PROPERTIES)!= null && mapFieldDSD.getValue().get(FOLLOW_PATTERN_PROPERTIES).get(TYPE_FIELD).asText().equals(STRING_TYPE)){

                                    return invokeMethodByReflection(null,returnedValue,true);
                                }

                            }
                            break;

                    }

                    break;

                case ENUM_FIELD:

                    System.out.println("enum");

                    break;


            }
        }


        return tempMap;

    }


    public Map<String, Object> getMetaDataCleaned() {
        return metaDataCleaned;
    }


    private JsonNode getMdsdObjectFromReference(String ref) {

        String[] path = ref.substring(2).split("/");

        JsonNode tmp = null;
        JsonNode referenceMdsd = mdsd;
        for (int i = 0; i < path.length; i++) {
            tmp = referenceMdsd.get(path[i]);
            referenceMdsd = tmp;
        }

        return referenceMdsd;
    }


    private Object invokeMethodByReflection(String fieldName, Object instanceToUse, boolean isMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object result = null;
        String methodString  = null;
        if(isMap){
            methodString = "get";
            Method method = instanceToUse.getClass().getMethod(methodString,Object.class);
            result = method.invoke(instanceToUse,(Object)LANG);

        }else{
            methodString = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = instanceToUse.getClass().getMethod(methodString, null);
            result = method.invoke(instanceToUse, null);
        }

        return result;
    }


}








